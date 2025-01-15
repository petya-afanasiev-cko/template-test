class ScenarioStep {
  constructor(name) {
    this.name = name;
    this.givenSteps = [];
    this.thenSteps = [];
    this.whenSteps = [];
  }

  given(name, testFn) {
    this.givenSteps.push({ name, testFn });
    return this;
  }

  then(name, testFn) {
    this.thenSteps.push({ name, testFn });
    return this;
  }

  when(name, testFn) {
    this.whenSteps.push({ name, testFn });
    return this;
  }
}

class ScenarioBuilder extends ScenarioStep {

  constructor() {
    super();
    this.scenarios = [];
    this.whenSteps = [];
  }

  addScenario(name, builderFunc) {
    let scenario = new ScenarioStep(name);
    builderFunc(scenario);
    this.scenarios.push(scenario);
    return this;
  }

  when(name, testFn) {
    this.whenSteps.push({ name, testFn });
    return this;
  }

  build() {
    this.scenarios.forEach(scenario =>
        this.buildScenario(scenario));
  }

  buildScenario(scenario) {
    let builder = this;
    Scenario(scenario.name, function () {
      // Shared Given Steps
      builder.givenSteps.forEach(function (step, i) {
        if (i == 0) {
          Given(step.name, step.testFn);
        } else {
          And(step.name, step.testFn);
        }
      });

      // Scenario Given Steps
      scenario.givenSteps.forEach(function (step, i) {
        if (i == 0 && builder.givenSteps.length == 0) {
          Given(step.name, step.testFn);
        } else {
          And(step.name, step.testFn);
        }
      });
      // Shared When Steps
      builder.whenSteps.forEach(function (step, i) {
        if (i == 0) {
          When(step.name, step.testFn);
        } else {
          And(step.name, step.testFn);
        }
      });

      // Scenario When Steps
      scenario.whenSteps.forEach(function (step, i) {
        if (i == 0 && builder.whenSteps.length == 0) {
          When(step.name, step.testFn);
        } else {
          And(step.name, step.testFn);
        }
      });
      // Shared Then Steps
      builder.thenSteps.forEach(function (step, i) {
        if (i == 0) {
          Then(step.name, step.testFn);
        } else {
          And(step.name, step.testFn);
        }
      });

      // Scenario Then Steps
      scenario.thenSteps.forEach(function (step, i) {
        if (i == 0 && builder.thenSteps.length == 0) {
          Then(step.name, step.testFn);
        } else {
          And(step.name, step.testFn);
        }
      });
    });
  }
}

module.exports = ScenarioBuilder;
