package pasuk

import (
	"bytes"
	"compress/gzip"
	"encoding/csv"
	"io"
	"log"
	"net/http"
	"strings"
)

// TODO var store [24000]string, Rest :8080, duplicates, etc...

func Reverse(s string) string {
	b := []byte(s)
	for i, j := 0, len(b)-1; i < len(b)/2; i, j = i+1, j-1 {
		b[i], b[j] = b[j], b[i]
	}
	return string(b)
}

func Pasuk(name string, containsName bool) int {

	resp, err := http.Get("https://raw.githubusercontent.com/shahart/heb-bible/master/bible.txt.gz")
	if err != nil {
		log.Fatal(err)
	}
	defer resp.Body.Close()

	content, err := io.ReadAll(resp.Body)
	if err != nil {
		log.Fatal(err)
	}

	// fmt.Println("Read", len(content), "bytes")

	siz := 0

	reader := bytes.NewReader(content)
	gr, err := gzip.NewReader(reader)
	if err != nil {
		log.Fatal(err)
	}
	defer gr.Close()

	cr := csv.NewReader(gr)
	rec, err := cr.Read()
	if err != nil {
		log.Fatal(err)
	}

	for err == nil {
		if err == nil && ((rec[1][1] == name[1] && rec[1][len(rec[1])-1] == name[len(name)-1]) || (containsName && strings.Contains(rec[1], name))) {
			siz++
			// fmt.Println(Reverse(rec[1]))
		}
		rec, err = cr.Read()
	}

	// fmt.Println(siz, " verses found")
	return siz
}
