#Create CI-CD Stack
echo "Enter CICD Stack Name:"
read STACK_NAME
aws cloudformation create-stack --stack-name $STACK_NAME --template-body file://csye6225-cf-ci-cd.json --capabilities CAPABILITY_NAMED_IAM
