Get to this point:

https://github.com/Netflix/PigPen/wiki/Tutorial


  (require '[pigpen.pig])
  (pigpen.pig/write-script "my-script.pig" (mypigrun "input.txt" "out.json"))



# lein uberjar
# cp target/uberjar/myapp-0.1.0-SNAPSHOT-standalone.jar pigpen.jar
# pig -f my-script.pig -p INPUT=input.txt -p OUTPUT=myout.json


  (require '[pigpen.pig])
  (pigpen.pig/write-script "my-script.pig" (mypigrun "$INPUT" "$OUTPUT"))

# /opt/hadoop/bin/hadoop fs -put pigpen.jar /user/root/pigpen.jar
# /opt/hadoop/bin/hadoop fs -put input.txt /user/root/input.txt
# pig -f my-script.pig -param INPUT="input.txt" -param OUTPUT="out.json"

$ aws s3 mb s3://bigwanna
$ aws s3 cp  pigpen.jar s3://bigwanna/pigpen.jar
$ aws s3 cp  input.txt s3://bigwanna/input.txt
$ aws s3 cp myscript.pig s3://bigwanna/myscript.pig
$ aws s3 ls s3://bigwanna
2017-05-23 05:47:57      73295 input.txt
2017-05-23 05:48:34       1752 myscript.pig
2017-05-23 05:47:28    9638011 pigpen.jar

$ aws emr create-default-roles

aws emr create-cluster --name "Pig Cluster" --release-label emr-5.5.0 --applications Name=Pig \
--use-default-roles --instance-type m3.xlarge --instance-count 1 --log-uri s3://bigwanna --auto-terminate \
--steps Type=PIG,Name="Pig Program",ActionOnFailure=CONTINUE,Args=[-f,s3://bigwannamyscript.pig,-p,INPUT=s3://bigwanna/input.txt,-p,OUTPUT=s3://bigwanna/output]

$ aws delet s3 ls s3://bigwanna/output/
2017-05-23 06:44:09          0 _SUCCESS
2017-05-23 06:44:09        358 part-v001-o000-r-00000
$ aws --profile delet s3 cp s3://bigwanna/output/part-v001-o000-r-00000 .
download: s3://bigwanna/output/part-v001-o000-r-00000 to ./part-v001-o000-r-00000

