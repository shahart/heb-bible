package pasuk_test

import (
	"example.com/pasuk"
	"fmt"
)

func ExampleShahar() {
	fmt.Println(pasuk.Pasuk("שחר", false))
	// Output: 25
}
