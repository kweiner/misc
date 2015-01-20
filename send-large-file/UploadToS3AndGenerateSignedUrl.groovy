/**
 * Use this script to upload a file to your Amazon S3 bucket
 * and generate an expiring signed URL.
 *
 * It is assumed you already have an Amazon AWS account and access to an S3 bucket.
 * Before running the script, configure the following below:
 *   pathToFile = the path to your file
 *   bucketName = the name of your S3 bucket
 *   expiresInHours = the amount of hours before your generated URL should expire
 *   myAccessKeyID = your Amazon access key ID
 *   mySecretKey = your Amazon secret key
 * Run this script with:
 *   groovy UploadToS3AndGenerateSignedUrl.groovy
 */

@Grab(group='com.amazonaws', module='aws-java-sdk', version='1.9.15')

import java.io.File;
import java.util.Date;
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.PutObjectRequest
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest

//////////////////////////////////////////////////////////////////////
// Configure these params first!!!
pathToFile = "/home/ken/myfile.zip"
bucketName = "mybucket"
expiresInHours = 48
myAccessKeyID = "my-access-key-id"
mySecretKey = "my-secret-key"
//////////////////////////////////////////////////////////////////////

// Initialize S3 Client
s3Client = new AmazonS3Client(new BasicAWSCredentials(myAccessKeyID, mySecretKey))

// Upload file to S3 and generate Signed URL
def objectKey = uploadFile(pathToFile)
def signedUrl = getSignedUrl(objectKey)

// Print out the full URL
println signedUrl

def uploadFile(String pathToFile) {
    def file = new File(pathToFile)
    def request = new PutObjectRequest(bucketName, file.name, file)
    s3Client.putObject(request)
    return file.name
}

def getSignedUrl(String objectKey) {
    // Set to 48 hours from now
    def expiresAtMillis = System.currentTimeMillis() +
        expiresInHours * 60 /*mins/hour*/ * 60 /*secs/min*/ * 1000 /*ms/sec*/
    def expiration = new Date(expiresAtMillis)

    // Setup the signed URL request
    def request = new GeneratePresignedUrlRequest(bucketName, objectKey)
    request.setExpiration(expiration)

    // Generate the signed URL
    def signedUrl = s3Client.generatePresignedUrl(request)
}


