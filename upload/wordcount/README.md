# edu.neu.csye6225.assign3

## required
* browser (eg. Chrome, firefox, Safari)
* AWS account
* genEc2.yaml
* Key : Name of an existing EC2 KeyPair to enable SSH access to the instance

## create a stack
1. open browser, login to AWS.
2. confirm you are in the "us-west-2" region.
3. go to CloudFormation Console.
4. click "create stack" >> "with new resources (standard)"
5. in step1 page:
    * select:
        * Prerequisite - Prepare template: Template is ready
        * Specify template: Upload a template file
            * upload file: genEc2.yaml
    * click "next" to step2 page.
6. in step2 page:
    * type in:
        * Stack name: [name it by yourself]
        * Parameters:
            * KeyName: [select your own]
    * click "next" to step3 page.
7. in step3 page:
    * Do nothing in this page.
    * click "next" to step4 page.
8. in step4 page:
    * scroll down the page, and confirm 1 checkboxs in "The following resource(s) require capabilities: [AWS::IAM::Role]"
    * click "create stack" to generate the app, waiting for the status is "CREATE_COMPLETE".
9. click the tab "output", there is a key named "WebsiteURL". It's the web's url, click its value link to go to the web.

## How to get words count
1. open browser, type the web url.
   the formula is = http://{yourDomain}/app?file={TextFileUrl}
    * this is the web link I made: (http://ec2-54-201-147-81.us-west-2.compute.amazonaws.com/app?file=https://www.gutenberg.org/cache/epub/19033/pg19033.txt)
2. wait for the page refresh, or refresh by hands, to show the output in the page.
3. see Screen Shot 2021-03-24 at 14.32.25.PNG
   ps. Since using tomcat, it would be wait for a long time to launch all apps, it would also be failure in the beginning.