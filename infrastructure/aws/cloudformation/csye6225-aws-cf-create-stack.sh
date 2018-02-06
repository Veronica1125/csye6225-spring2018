#!/bin/bash

set -e
#Author: Xiao Li
echo "Author: Xiao Li"
echo "        li.xiao5@husky.neu.edu"
#Usage: setting up our networking resources such as Virtual Private Cloud (VPC), Internet Gateway, Route Table and Routes using AWS Cloud Formation

#Arguments: STACK_NAME

STACK_NAME=$1
#Create All steps:
aws cloudformation create-stack --stack-name $STACK_NAME --template-body file://csye6225/dev/csye6225-spring2018/infrastructure/aws/cloudformation/csye6225-cf-networking.json


#Job Done
echo "Job Done!"
