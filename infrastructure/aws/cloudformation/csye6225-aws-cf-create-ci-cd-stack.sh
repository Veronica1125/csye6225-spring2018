#Create CI-CD Stack
echo "Enter CICD Stack Name:"
read STACK_NAME
export ACCOUNT_ID=$(aws sts get-caller-identity --query "Account" --output text)
export REGION=us-east-1
export APPLICATION_NAME=csye6225-webapp
export RESOURCE1="arn:aws:codedeploy:"$REGION":"$ACCOUNT_ID":application:"$APPLICATION_NAME
export RESOURCE2="arn:aws:codedeploy:"$REGION":"$ACCOUNT_ID":deploymentconfig:CodeDeployDefault.OneAtATime"
export RESOURCE3="arn:aws:codedeploy:"$REGION":"$ACCOUNT_ID":deploymentconfig:CodeDeployDefault.HalfAtATime"
export RESOURCE4="arn:aws:codedeploy:"$REGION":"$ACCOUNT_ID":deploymentconfig:CodeDeployDefault.AllAtOnce"

aws cloudformation create-stack --stack-name $STACK_NAME --template-body file://csye6225-cf-ci-cd.json --capabilities CAPABILITY_NAMED_IAM --parameters ParameterKey=RESOURCE1,ParameterValue=$RESOURCE1 ParameterKey=RESOURCE2,ParameterValue=$RESOURCE2 ParameterKey=RESOURCE3,ParameterValue=$RESOURCE3 ParameterKey=RESOURCE4,ParameterValue=$RESOURCE4
