set -e

#Usage: Taking STACK_NAME as parameter and building a vpc, internet gateway, route table and route through aws cloudformation

echo "Enter Application Stack Name:"
read STACK_NAME
#STACK_NAME=$1

echo "Enter NetWork Stack Name:"
read NETWORK_STACK_NAME

VpcId=`aws ec2 describe-vpcs --filter "Name=tag:Name,Values=${NETWORK_STACK_NAME}-csye6225-vpc" --query 'Vpcs[*].{id:VpcId}' --output text`
echo "Vpc found: "$VpcId

ParamVpcId=$VpcId

z_id=$(aws route53 list-hosted-zones --query 'HostedZones[0].Id' --output text)

z_id=${z_id#*e/}

NAME=$(aws route53 list-hosted-zones --query "HostedZones[0].Name" --output text)

#DBServerSecurityGroupID=`aws rds describe-db-security-groups --filters "Name=VpcId, Values=${VpcId}" --query "DBSecurityGroups[*].DBSecurityGroupName" --output text`
DBServerSecurityGroupID=`aws ec2 describe-security-groups --filters "Name=tag:aws:cloudformation:logical-id, Values=DBServerSecurityGroup" --query "SecurityGroups[*].GroupId" --output text`

WebServerSecurityGroupID=`aws ec2 describe-security-groups --filters "Name=tag:aws:cloudformation:logical-id, Values=WebServerSecurityGroup" --query "SecurityGroups[*].GroupId" --output text`

LoadBalancerSecurityGroupID=`aws ec2 describe-security-groups --filters "Name=tag:aws:cloudformation:logical-id, Values=LoadBalancerSecurityGroup" --query "SecurityGroups[*].GroupId" --output text`
#DBServerSecurityGroupID=`aws ec2 describe-security-groups --filters "Name=tag:aws:cloudformation:logical-id, Values=DBServerSecurityGroup" --query "SecurityGroups[*].GroupId" --output text`

WEBSERVERSUBNETID=`aws ec2 describe-subnets --filters "Name=tag:Name, Values=SubnetForWebServers" --query "Subnets[*].SubnetId" --output text`
#echo $WEBSERVERSUBNETID 

DBSERVERSUBNETID=`aws ec2 describe-subnets --filters "Name=tag:Name, Values=SubnetForDBServers" --query "Subnets[*].SubnetId" --output text`
#echo $DBSERVER_SUBNET_ID

ParamWEBSERVERSUBNETID=$WEBSERVERSUBNETID
#echo $ParamWEBSERVER_SUBNET_ID

ParamDBSERVERSUBNETID=$DBSERVERSUBNETID
#echo $ParamDBSERVER_SUBNET_ID

DBUser=root

DBPassword=12345678

LoadBalancerName="csyeLoadBalancer"

TagKey=Name

TagValue=csye6225

export ACCOUNT_ID=$(aws sts get-caller-identity --query "Account" --output text)

export S3BucketName=${ACCOUNT_ID}s3-code-deploy-csye6225.com

export TOPIC_NAME=password_reset

export CERTIFICATE_ARN=$(aws acm list-certificates --query "CertificateSummaryList[0].CertificateArn" --output text)

#Create Stack
aws cloudformation create-stack --stack-name $STACK_NAME --template-body file://csye6225-cf-application.json --capabilities CAPABILITY_IAM --parameters ParameterKey=ParamWEBSERVERSUBNETID,ParameterValue=$ParamWEBSERVERSUBNETID ParameterKey=ParamDBSERVERSUBNETID,ParameterValue=$ParamDBSERVERSUBNETID ParameterKey=WebServerSecurityGroupID,ParameterValue=$WebServerSecurityGroupID ParameterKey=DBServerSecurityGroupID,ParameterValue=$DBServerSecurityGroupID ParameterKey=DBUser,ParameterValue=$DBUser ParameterKey=DBPassword,ParameterValue=$DBPassword ParameterKey=S3BucketName,ParameterValue=$S3BucketName ParameterKey=TopicName,ParameterValue=$TOPIC_NAME ParameterKey=ParamVpcId,ParameterValue=$VpcId ParameterKey=CertificateArn1,ParameterValue=$CERTIFICATE_ARN ParameterKey=LoadBalancerName,ParameterValue=$LoadBalancerName ParameterKey=TagKey,ParameterValue=$TagKey ParameterKey=TagValue,ParameterValue=$TagValue ParameterKey=LoadBalancerSecurityGroupID,ParameterValue=$LoadBalancerSecurityGroupID ParameterKey=HostedZoneId,ParameterValue=$z_id ParameterKey=Name,ParameterValue=$NAME
#Check Stack Status
STACK_STATUS=`aws cloudformation describe-stacks --stack-name $STACK_NAME --query "Stacks[][ [StackStatus ] ][]" --output text`

#Wait until stack completely created
echo "Please wait..."

while [ $STACK_STATUS != "CREATE_COMPLETE" ]
do
	STACK_STATUS=`aws cloudformation describe-stacks --stack-name $STACK_NAME --query "Stacks[][ [StackStatus ] ][]" --output text`
done

#Job Done!
echo "Job Done!"

