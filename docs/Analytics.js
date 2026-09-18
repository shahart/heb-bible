import { ANALYTICS_URL } from "./AppConfig.js";

async function reportUsage(type, name, extra = "") {
    try {
        const response = await fetch(ANALYTICS_URL, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ name, extra, type }),
            keepalive: true
        });

        if (!response.ok) {
            console.debug(`Usage report failed: ${response.status}`);
        }
    }
    catch (error) {
        // Reporting must never interrupt the user's search.
        console.debug(`Usage report unavailable: ${error}`);
    }
}

export { reportUsage };
