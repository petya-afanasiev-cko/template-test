const { S3Client, PutObjectCommand } = require("@aws-sdk/client-s3");
const fs= require('node:fs');
const dockerHelper = require('./docker-helper');

module.exports = {
  uploadFile
};

const s3Client = new S3Client({
  region: "eu-west-1",
  endpoint: config.awsUrl,
  forcePathStyle: true
});

async function uploadFile(filePath) {
  const fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
  await s3Client.send(
      new PutObjectCommand({
        Bucket: config.s3Bucket,
        Key: config.s3BucketDir + fileName,
        Body: fs.createReadStream(filePath),
      })
  );
  await dockerHelper.runContainer(fileName);
}
