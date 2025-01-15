module.exports = getConfig();

function getConfig() {
	const fileName = getConfigFileName();
	console.log("Using configuration " + fileName);
	return require("./" + fileName);
}

function getConfigFileName() {
	const env =  process.env.ENV || "local";
	return env + ".json";
}