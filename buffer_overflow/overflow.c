
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

/*
 * gcc -fno-stack-protector -z execstack -g overflow.c -o overflow
 * gdb overflow; disas Done
 */

void Done(void);
const char * const BrokenFunction(void);

int main()
{
	printf("You wrote: '%s'\n", BrokenFunction());
	return 0;
} 

const char * const BrokenFunction(void)
{
	char buf[128];
	gets(buf); // unsafe function call!!
	return strdup(buf); 
}

void Done(void)
{
	fflush(stdout);
	printf("Got it!\n");
	exit(0);
}
