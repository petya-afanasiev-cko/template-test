const { exec } = require('child_process');

module.exports = {
  runContainer
};

async function runContainer(fileName) {
  cmd = `docker run --rm --name {{ cookiecutter.scheme_slug }}-settlement --network {{ cookiecutter.scheme_slug }}-settlement-network -e AWS_ACCESS_KEY_ID=xxx -e AWS_SECRET_ACCESS_KEY=xxx -e LOCALSTACK_AWS_ENDPOINT=localstack {{ cookiecutter.scheme_slug }}-settlement --s3_bucket=${config.s3Bucket} --s3_key=${config.s3BucketDir}${fileName}`;
  console.log(cmd)
  const { stdout, stderr } = await exec(cmd);
}