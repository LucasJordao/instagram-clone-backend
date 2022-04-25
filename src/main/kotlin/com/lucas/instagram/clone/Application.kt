package com.lucas.instagram.clone

import io.micronaut.runtime.Micronaut.*
fun main(args: Array<String>) {
	build()
	    .args(*args)
		.packages("com.lucas.instagram.clone")
		.start()
}

