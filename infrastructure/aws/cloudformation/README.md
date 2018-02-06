#Instructions to Run the Script

"csye6225-aws-cf-create-stack.sh" Script will

    Create a cloudformation stack taking STACK_NAME as parameter
    Create and configure required networking resources
    Create a Virtual Private Cloud (VPC) resource called STACK_NAME-csye6225-vpc
    Create Internet Gateway resource called STACK_NAME-csye6225-InternetGateway
    Attach the Internet Gateway to STACK_NAME-csye6225-vpc VPC
    Create a public Route Table called STACK_NAME-csye6225-public-route-table
    Create a public route in STACK_NAME-csye6225-public-route-table route table with destination CIDR block 0.0.0.0/0 and STACK_NAME-csye6225-InternetGateway as the target

"csye6225-aws-cf-terminate-stack.sh" Script will

    Delete the stack and all networking resources. Script should take STACK_NAME as parameter

"csye6225-cf-networking.json"

    The cloudFormation template for this stack

