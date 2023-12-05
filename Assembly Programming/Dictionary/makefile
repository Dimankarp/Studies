##
# Dictionary based on a Linked List
#
TARGET = main
SRCS = $(wildcard ./*.asm)
OBJDIR = obj
OBJS = $(patsubst ./%.asm, $(OBJDIR)/%.o, $(SRCS))
DEPS = $(patsubst ./%.asm, $(OBJDIR)/%.d, $(SRCS))
ASM_FLGS = -f elf64
$(OBJDIR)/%.d: %.asm
	mkdir -p $(OBJDIR)
	nasm $(ASM_FLGS) -M -MF $@ $<

$(OBJDIR)/%.o: %.asm $(OBJDIR)/%.d
	mkdir -p $(OBJDIR)
	nasm $(ASM_FLGS) -o $@ $<

$(TARGET): $(OBJS)
	ld -o $(TARGET) $(OBJS)

include $(DEPS)

.PHONY: all clean test

all: $(TARGET)

clean:
	rm -rf $(TARGET) $(OBJDIR) report.xml

test:
	python3 test.py



# end
