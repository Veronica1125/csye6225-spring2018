# Instructions to Run the Script
<p>"csye6225-aws-networking-setup.sh" Script will</p>
<ul>
	<li>create and configure required networking resources using AWS CLI. Script should take STACK_NAME as parameter</li>
	<li>Create a Virtual Private Cloud (VPC)</li>
	<li>Create Internet Gateway resource</li>
	<li>Attach the Internet Gateway to the created VPC</li>
	<li>Create a public Route Table</li>
	<li>Create a public route in the public route table created above with destination CIDR block 0.0.0.0/0 and internet gateway creted above as the target</li>
</ul>
<p>"csye6225-aws-networking-teardown.sh" Script will</p>
<ul>
	<li>delete networking resources using AWS CLI. Script should take STACK_NAME as parameter</li>
</ul>
