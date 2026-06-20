package pasuk

import (
	"bytes"
	"compress/gzip"
	"encoding/csv"
	"io"
	"log"
	"net/http"
	"strings"
	"sync"
)

// Cached verses loaded once to avoid repeated network fetches.
var (
	verses []string
	once   sync.Once
	ldbErr error
)

func loadBible() {
	resp, err := http.Get("https://raw.githubusercontent.com/shahart/heb-bible/master/bible.txt.gz")
	if err != nil {
		ldbErr = err
		log.Println("loadBible: http get:", err)
		return
	}
	defer resp.Body.Close()

	content, err := io.ReadAll(resp.Body)
	if err != nil {
		ldbErr = err
		log.Println("loadBible: read all:", err)
		return
	}

	reader := bytes.NewReader(content)
	gr, err := gzip.NewReader(reader)
	if err != nil {
		ldbErr = err
		log.Println("loadBible: gzip new reader:", err)
		return
	}
	defer gr.Close()

	cr := csv.NewReader(gr)
	recs, err := cr.ReadAll()
	if err != nil && err != io.EOF {
		ldbErr = err
		log.Println("loadBible: csv read all:", err)
		return
	}

	log.Println("loadBible: total recs:", len(recs))

	for _, rec := range recs {
		if len(rec) > 1 {
			verses = append(verses, rec[1])
		}
	}
}

func ensureLoaded() error {
	once.Do(loadBible)
	return ldbErr
}

// TODO var store [24000]string, Rest :8080, duplicates, etc...

func Reverse(s string) string {
	b := []byte(s)
	for i, j := 0, len(b)-1; i < len(b)/2; i, j = i+1, j-1 {
		b[i], b[j] = b[j], b[i]
	}
	return string(b)
}

func Pasuk(name string, containsName bool) int {
	if name == "" {
		return 0
	}
	if err := ensureLoaded(); err != nil {
		log.Println("Pasuk: failed to load bible:", err)
		return 0
	}

	siz := 0
	for _, txt := range verses {
		// ensure lengths before indexing to avoid panics
		if containsName && strings.Contains(txt, name) {
			siz++
			continue
		}
		if len(txt) > 1 && len(name) > 1 {
			if txt[1] == name[1] && txt[len(txt)-1] == name[len(name)-1] {
				siz++
			}
		}
	}
	return siz
}
