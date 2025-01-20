const { exec } = require('child_process');

module.exports = {
  runContainer
};

async function runContainer(fileName) {
  cmd = `docker run --rm --name {{ cookiecutter.project_slug }} --network {{ cookiecutter.project_slug }}-network -e AWS_ACCESS_KEY_ID=xxx -e AWS_SECRET_ACCESS_KEY=xxx -e LOCALSTACK_AWS_ENDPOINT=localstack -e KAFKA_BOOTSTRAP_SERVERS=kafka:9091 -e SCHEMA_REGISTRY_ENDPOINT=schema-registry {{ cookiecutter.project_slug }} --s3_bucket=${config.s3Bucket} --s3_key=${config.s3BucketDir}${fileName}`;
  console.log(cmd)
  const { stdout, stderr } = await exec(cmd);
  for await (const chunk of stdout) {
    console.log(chunk)
  }
}