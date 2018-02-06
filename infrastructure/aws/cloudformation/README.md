# Instructions to Run the Script
<p>"csye6225-aws-cf-create-stack.sh" Script will</p>
<ul>
  <li>Create a cloudformation stack taking STACK_NAME as parameter</li>
	<li>Create and configure required networking resources</li>
	<li>Create a Virtual Private Cloud (VPC) resource called STACK_NAME-csye6225-vpc</li>
	<li>Create Internet Gateway resource called STACK_NAME-csye6225-InternetGateway</li>
	<li>Attach the Internet Gateway to STACK_NAME-csye6225-vpc VPC</li>
	<li>Create a public Route Table called STACK_NAME-csye6225-public-route-table</li>
	<li>Create a public route in STACK_NAME-csye6225-public-route-table route table with destination CIDR block 0.0.0.0/0 and STACK_NAME-csye6225-InternetGateway as the target</li>
</ul>
<p>"csye6225-aws-cf-terminate-stack.sh" Script will</p>
<ul>
	<li>Delete the stack and all networking resources. Script should take STACK_NAME as parameter</li>
</ul>
