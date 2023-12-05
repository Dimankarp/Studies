CFLAGS     = -g -O2 -Wall -Werror -std=c17 -Wno-unused-function -Wdiscarded-qualifiers -Wincompatible-pointer-types -Wint-conversion -fno-plt
CC         = gcc
LD         = gcc
TARGET     = printer

all: $(TARGET)

$(TARGET): ast.o main.o tokenizer.o
	$(LD) -o $@ $^

%.o: %.c
	$(CC) -c $(CFLAGS) -o $@ $<

main.o: main.c ast.h tokenizer.h ring.h

ast.o: ast.c ast.h

tokenizer.o: tokenizer.c tokenizer.h ring.h

clean: 
	$(RM) $(TARGET) *.o

run: $(TARGET)
	./$(TARGET)

.PHONY: clean all run
