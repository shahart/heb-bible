const BOOK_ALIASES = [
    ["Joshua", 6],
    ["Judges", 7],
    ["I Samuel", 8],
    ["II Samuel", 9],
    ["I Kings", 10],
    ["II Kings", 11],
    ["Isaiah", 12],
    ["Jeremiah", 13],
    ["Ezekiel", 14],
    ["Hosea", 15],
    ["Joel", 16],
    ["Amos", 17],
    ["Obadiah", 18],
    ["Jonah", 19],
    ["Micha", 20],
    ["Micah", 20],
    ["Nachum", 21],
    ["Habakkuk", 22],
    ["Zephaniah", 23],
    ["Haggai", 24],
    ["Zechariah", 25],
    ["Malachi", 26]
];

function parseExternalBookReference(value) {
    if (!value || !value.trim()) return null;

    const normalized = value.trim();
    const alias = BOOK_ALIASES.find(([name]) => normalized.startsWith(name));
    if (!alias) return null;

    const chapter = normalized.split(/\s+/).at(-1).split(":")[0];
    if (!/^\d+$/.test(chapter)) return null;

    return `${alias[1]},${chapter}`;
}

export { parseExternalBookReference };
