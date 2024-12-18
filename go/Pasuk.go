package main

import (
	"compress/gzip"
	"encoding/csv"
	"fmt"
	"log"
	"os"
	"strings"
)

// TODO var store [24000]string

func main() {

	siz := 0
	name := os.Args[1]

	f, err := os.Open("../bible.txt.gz")
	if err != nil {
		log.Fatal(err)
	}
	defer f.Close()

	gr, err := gzip.NewReader(f)
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
		rec, err = cr.Read()
		// TODO if line[0] == name[0] && line[len(line)-1] == name[len(name)-1] {
		if err == nil && strings.Contains(rec[1], name) {
			siz++
			// fmt.Println(siz, ":", rec[0], rec[1])
		}
	}

	fmt.Println(siz, " verses found")
}
