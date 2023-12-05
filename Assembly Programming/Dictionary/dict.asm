    %include "lib.inc"

    global find_word
    section .text
    ;; rdi - key string ref, rsi - dictionary ref
find_word:
    push r12
    push r13
    mov r12, rdi
    mov r13, rsi
.loop:
    mov rdi, r12
    mov rsi, [r13+8]
    call string_equals
    test rax, rax               ;string_equals returns 1 if strings are equal, 0 - othrerwise
    jnz .end
    mov r13, [r13]
    test r13, r13
    jz .end
    jmp .loop
.end:
    mov rax, r13
    pop r13
    pop r12
    ret
