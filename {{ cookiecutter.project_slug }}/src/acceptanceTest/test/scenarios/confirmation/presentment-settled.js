const
    ScenarioBuilder = require("../../scenario-builder"),
    s3Helper = require("../../helpers/s3-helper"),
    kafkaHelper = require("../../helpers/kafka-helper");

feature("Confirmation file @presentment-settled", function () {
  let context;

  beforeEachScenario(async () => {
    context = {};
    await kafkaHelper.startConsumer();
  });

  new ScenarioBuilder()
  .addScenario("Valid 'PresentmentSettled' Capture emitted @capture", s => s
      .given("a valid Confirmation file", () => {
        context.incomingFile = config.confirmationFile;
      })
      .when("file is uploaded to S3 and processed", async () => {
        await s3Helper.uploadFile(context.incomingFile);
        context.events = await kafkaHelper.getEvents();
        // todo verify settlement and raw events
      })
      .then("'PresentmentSettled' Capture is valid", async () => {
        console.log(context.events)
      })
  ).build();

});