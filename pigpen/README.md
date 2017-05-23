# Running PigPen on AWS EMR

## Create an initial Pig script

The processes in the below guide should assist in creating an initial Pig script:

[https://github.com/Netflix/PigPen/wiki/Tutorial](https://github.com/Netflix/PigPen/wiki/Tutorial)

We strongly recommend performing your initial development and testing on a local instance, to ensure the code is functional before deploying an EMR cluster. You may wish to investigate pre-built Docker containers to simplify this. Generate and save your script as follows:

``` clj
  (require '[pigpen.pig])
  (pigpen.pig/write-script "my-script.pig" (mypigrun "input.txt" "out.json"))
```

And then run your script as follows:

``` bash

$ lein uberjar
$ cp target/uberjar/myapp-0.1.0-SNAPSHOT-standalone.jar pigpen.jar
$ pig -x local -f my-script.pig

```

## Parameterise your Pig script

Your next step is to recreate your Pig script in a fashion suited to deployment. The following will run this in a full HDFS environment:

``` clj
  (require '[pigpen.pig])
  (pigpen.pig/write-script "my-script.pig" (mypigrun "$INPUT" "$OUTPUT"))
```

``` bash
# /opt/hadoop/bin/hadoop fs -put pigpen.jar /user/root/pigpen.jar
# /opt/hadoop/bin/hadoop fs -put input.txt /user/root/input.txt
# pig -f my-script.pig -param INPUT="input.txt" -param OUTPUT="out.json"

```

## Deploy to EMR

The processes below can of course be done using the GUI. However, in order to present a repeatable process, these examples utilise the AWS CLI.

Start by creating an S3 bucket. It's safest to create a dedicated bucket to the EMR process. The bucket will require the files created earlier, and your input file.

Although Netflix note the generated .pig scripts are not meant for human editing, there is a change that needs to be made to the generated script. Your very first line will be:

```
REGISTER pigpen.jar
```
This will need to be changed to the full S3 path, so Pig can find it:
```
REGISTER s3://examplebucket/pigpen.jar;
```
You may consider automating this with a small script. You can then proceed to create a bucket and upload the relevant files:
``` bash
$ aws s3 mb s3://examplebucket
$ aws s3 cp pigpen.jar s3://examplebucket/pigpen.jar
$ aws s3 cp input.txt s3://examplebucket/input.txt
$ aws s3 cp myscript.pig s3://examplebucket/myscript.pig
$ aws s3 ls s3://examplebucket
2017-05-23 05:47:57     973295 input.txt
2017-05-23 05:48:34       1752 myscript.pig
2017-05-23 05:47:28    9638011 pigpen.jar

```

Note we did not create the output directory. This *must not* exist.
If this is the first cluster you have run, you will now need to run the following:

``` bash
$ aws emr create-default-roles
```

This command will then:
- Launch an EMR cluster
- Use 3 m3.xlarge instances
- Log to the S3 bucket
- Shutdown and terminate the cluster when the process is done

``` bash
$ aws emr create-cluster --name "Pig Cluster" --release-label emr-5.5.0 --applications Name=Pig \
--use-default-roles --instance-type m3.xlarge --instance-count 3 --log-uri s3://examplebucket --auto-terminate \
--steps Type=PIG,Name="Pig Program",ActionOnFailure=CONTINUE,Args=[-f,s3://examplebucket/myscript.pig,-p,INPUT=s3://examplebucket/input.txt,-p,OUTPUT=s3://examplebucket/output]
```

Finally, download the output from the S3 bucket:

``` bash
$ aws s3 ls s3://examplebucket/output/
2017-05-23 06:44:09          0 _SUCCESS
2017-05-23 06:44:09        358 part-v001-o000-r-00000
$ aws s3 cp s3://examplebucket/output/part-v001-o000-r-00000 .
download: s3://examplebucket/output/part-v001-o000-r-00000 to ./part-v001-o000-r-00000
```
