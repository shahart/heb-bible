## Review findings

- **P2 — Lunr searches can show a contradictory “not found” message.** When Lunr mode is checked, [Find.js](/Users/shahart/repos/heb-bible/docs/Find.js:22) renders Lunr’s results, then continues with the literal substring search. A query like `+*שחר +*טל` produces a Lunr result but no literal match, so the page appends “חיפוש רגיל - לא נמצא” and reports `found-false` to analytics. The current regression test calls `Repo.lucene()` directly, so it doesn’t catch this UI-path issue.

- **P2 — Search queries are sent to an external analytics endpoint.** [Find.js](/Users/shahart/repos/heb-bible/docs/Find.js:14) passes the raw query to `reportUsage`; [Analytics.js](/Users/shahart/repos/heb-bible/docs/Analytics.js:2) sends it in a POST request to the Lambda URL. Search terms may include personal names. Consider disclosing this clearly or sending only aggregate counts.

- **P2 — The Hebrew “stemmer” doesn’t stem words.** [lunr.he.js](/Users/shahart/repos/heb-bible/docs/lunr.he.js:82) runs only `cleanWord`; for Hebrew tokens that function leaves the word unchanged. Lunr’s query syntax, fuzzy search, and ranking work, but users shouldn’t expect Hebrew word-form normalization.

- **P2 — Search results use `innerHTML` with generated strings.** [Repo.js](/Users/shahart/repos/heb-bible/docs/Repo.js:165) embeds verse text and link markup into Lunr’s result reference, then [inserts it as HTML](/Users/shahart/repos/heb-bible/docs/Repo.js:198). The current bundled Bible text contains no angle brackets, so I found no direct XSS from the present data. Building result text and links as DOM nodes would avoid relying on that assumption.

- **P3 — Search controls lack search semantics.** The search UI in [index.html](/Users/shahart/repos/heb-bible/docs/index.html:261) uses a `<div>`, a plain text input, and a button rather than a `<search>` landmark and named search form. It has a visible label and Enter-key handling, but assistive technology and browsers miss the search landmark and native search-input behavior.

The previous browser suite passed, but its tests don’t cover the combined Lunr checkbox flow described above. This was a review only; I made no changes.

### Modern Web Guidance

I checked the security, HTML sanitization, forms, and accessibility guidance. I used it to assess the HTML sinks, query handling, and search controls; I reported the findings without changing code.
