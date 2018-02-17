set -e
#Author Xiao Li
echo "Author:Yang Yuan"
echo "	      yuan.yang@husky.neu.edu"
#Usage: Taking STACK_NAME as parameter and building a vpc, internet gateway, route table and route through aws cloudformation

STACK_NAME=$1

#Create Stack
aws cloudformation create-stack --stack-name $STACK_NAME --template-body file://csye6225-cf-application.json
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
