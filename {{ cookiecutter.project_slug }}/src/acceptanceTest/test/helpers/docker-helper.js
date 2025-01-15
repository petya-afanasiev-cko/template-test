const { exec } = require('child_process');

module.exports = {
  runContainer
};

async function runContainer(fileName) {
  cmd = `docker run --rm --name dci-settlement --network dci-settlement-network -e AWS_ACCESS_KEY_ID=xxx -e AWS_SECRET_ACCESS_KEY=xxx -e LOCALSTACK_AWS_ENDPOINT=localstack dci-settlement --s3_bucket=${config.s3Bucket} --s3_key=${config.s3BucketDir}${fileName}`;
  console.log(cmd)
  const { stdout, stderr } = await exec(cmd);
}