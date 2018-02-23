set -e

#Author Xiao Li
echo "Author: Xiao Li"
echo "	      li.xiao5@husky.neu.edu"

#Usage: Taking STACK_NAME as parameter and building a vpc, internet gateway, route table and route through aws cloudformation

echo "Enter Application Stack Name:"
read STACK_NAME
#STACK_NAME=$1

echo "Enter NetWork Stack Name:"
read NETWORK_STACK_NAME

VpcID=`aws ec2 describe-vpcs --filter "Name=tag:Name,Values=${NETWORK_STACK_NAME}-csye6225-vpc" --query 'Vpcs[*].{id:VpcId}' --output text`
echo "Vpc found: "$VpcID
#ParamVpcID=$VpcID
DBServerSecurityGroupID=`aws rds describe-db-security-groups --filters "Name=VpcId, Values=${VpcId}" --query "DBSecurityGroups[1].DBSecurityGroupName" --output text`

WebServerSecurityGroupID=`aws ec2 describe-security-groups --filters "Name=tag:aws:cloudformation:logical-id, Values=WebServerSecurityGroup" --query "SecurityGroups[*].GroupId" --output text`

#DBServerSecurityGroupID=`aws ec2 describe-security-groups --filters "Name=tag:aws:cloudformation:logical-id, Values=DBServerSecurityGroup" --query "SecurityGroups[*].GroupId" --output text`

WEBSERVERSUBNETID=`aws ec2 describe-subnets --filters "Name=tag:Name, Values=SubnetForWebServers" --query "Subnets[*].SubnetId" --output text`
#echo $WEBSERVERSUBNETID 

DBSERVERSUBNETID=`aws ec2 describe-subnets --filters "Name=tag:Name, Values=SubnetForDBServers" --query "Subnets[*].SubnetId" --output text`
#echo $DBSERVER_SUBNET_ID

ParamWEBSERVERSUBNETID=$WEBSERVERSUBNETID
#echo $ParamWEBSERVER_SUBNET_ID

ParamDBSERVERSUBNETID=$DBSERVERSUBNETID
#echo $ParamDBSERVER_SUBNET_ID

#Create Stack
aws cloudformation create-stack --stack-name $STACK_NAME --template-body file://csye6225-cf-application.json --parameters ParameterKey=ParamWEBSERVERSUBNETID,ParameterValue=$ParamWEBSERVERSUBNETID ParameterKey=ParamDBSERVERSUBNETID,ParameterValue=$ParamDBSERVERSUBNETID ParameterKey=WebServerSecurityGroupID,ParameterValue=$WebServerSecurityGroupID ParameterKey=DBServerSecurityGroupID,ParameterValue=$DBServerSecurityGroupID

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
