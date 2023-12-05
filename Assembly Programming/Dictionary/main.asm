    %include "lib.inc"
    %include "dict.inc"
    %include "words.inc"
    %xdefine BUF_SIZE 256
    %xdefine GENERAL_ERROR 1
    global _start
    section .rodata
err_msg_dict:  db  "ERR: Couldn't find entered key in the dictionary!", 10, 0
err_msg_input: db   "ERR: Couldn't read key from STDIN!", 10, 0

    section .bss
input:  resb BUF_SIZE
    section .text
_start:
    mov rdi, input
    mov rsi, BUF_SIZE-1         ;leaving space for a null-term
    call read_string
    test rax, rax
    jz .read_err
    mov rdi, rax
    mov rsi, last_ref           ;last_ref - macro from colon.inc
    call find_word
    test rax, rax
    jz .dict_err
.print:
    mov rdi, [rax+16]
    call print_string
    xor rdi, rdi
    jmp .exit
.read_err:
    mov rdi, err_msg_input
    jmp .err
.dict_err:
    mov rdi, err_msg_dict
.err:
    call print_error
    mov rdi, GENERAL_ERROR
.exit:
    call exit
