const APP_BASE_URL = "https://shahart.github.io/heb-bible/";
const ANALYTICS_URL = "https://z4r74tvfwdi3wywr4aegh4f3di0zhhuo.lambda-url.eu-north-1.on.aws/";

function createAppUrl(params = {}) {
    const url = new URL(APP_BASE_URL);
    for (const [key, value] of Object.entries(params)) {
        if (value !== undefined && value !== null && value !== "") {
            url.searchParams.set(key, String(value));
        }
    }
    return url.toString();
}

function createReferenceUrl(book, chapter) {
    return createAppUrl({ r: `${book},${chapter}` });
}

export { ANALYTICS_URL, APP_BASE_URL, createAppUrl, createReferenceUrl };
