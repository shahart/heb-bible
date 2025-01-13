package main

import (
	"example.com/pasuk"
	"fmt"
	"os"
	"strconv"
)

func main() {
	name := os.Args[1]
	containsName := false
	if len(os.Args) > 2 {
		containsName, _ = strconv.ParseBool(os.Args[2])
		// if _ != nil {
		// fmt.Println("Ignoring 2nd param containsName:", err)
		// }
	}
	fmt.Println(pasuk.Pasuk(name, containsName), "verses found")
}
