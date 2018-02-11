set -e
#Author: Xiao Li
echo "Author: Xiao Li"
echo "        li.xiao5@husky.neu.edu"
#Usage: setting up our networking resources such as Virtual Private Cloud (VPC), Internet Gateway, Route Table and Routes using AWS Cloud Formation

STACK_NAME=$1

#Create Stack:
aws cloudformation create-stack --stack-name $STACK_NAME --template-body file://csye6225-cf-networking.json

#Check Stack Status
STACK_STATUS=`aws cloudformation describe-stacks --stack-name $STACK_NAME --query "Stacks[][ [StackStatus ] ][]" --output text`

#Wait until stack completely created
echo "Please wait..."

while [ $STACK_STATUS != "CREATE_COMPLETE" ]
do
	STACK_STATUS=`aws cloudformation describe-stacks --stack-name $STACK_NAME --query "Stacks[][ [StackStatus ] ][]" --output text`
done

#Find vpc Id
vpcId=`aws ec2 describe-vpcs --filter "Name=tag:Name,Values=${STACK_NAME}" --query 'Vpcs[*].{id:VpcId}' --output text`
#Rename vpc
aws ec2 create-tags --resources $vpcId --tags Key=Name,Value=$STACK_NAME-csye6225-vpc

#Find Internet Gateway
gatewayId=`aws ec2 describe-internet-gateways --filter "Name=tag:Name,Values=${STACK_NAME}" --query 'InternetGateways[*].{id:InternetGatewayId}' --output text`
#Rename Internet Gateway
aws ec2 create-tags --resources $gatewayId --tags Key=Name,Value=$STACK_NAME-csye6225-InternetGateway

#Find Route Table
routeTableId=`aws ec2 describe-route-tables --filter "Name=tag:Name,Values=${STACK_NAME}" --query 'RouteTables[*].{id:RouteTableId}' --output text` 
#Rename Route Table
aws ec2 create-tags --resources $routeTableId --tags Key=Name,Value=$STACK_NAME-csye6225-public-route-table

#Job Done!
echo "Job Done!"
