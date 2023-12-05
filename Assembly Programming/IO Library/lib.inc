    global exit, string_length, print_string, print_char
    global print_newline, print_uint, print_int, string_equals
    global read_char, read_word, parse_uint, parse_int, string_copy

    ;; Defining magic numbers
    %define EXIT_CALL 60
    %define WRITE_CALL 1
    %define READ_CALL 0
    %define UINT_MAX_DIGITS 20
    %define MIN_INT 0x8000000000000000
    %define DECIMAL_BASE 10
    %define STD_OUT 1
    %define STD_IN 0
    %define ASCII_DIGIT_OFFSET 0x30
    ;; Most of the comments are in English, because
    ;; bloody Ubuntu doesn't allow users to set layout
    ;; switch hotkey to SHIFT+ALT. А я мириться не намерен!

    section .text

    ;; Принимает код возврата и завершает текущий процесс
    ;; Код возврата в rdi как первый аргумент
exit:
    mov rax, EXIT_CALL
    syscall
    ;; Принимает указатель на нуль-терминированную строку, возвращает её длину
    ;; Указатель в rdi как первый аргумент
string_length:
    xor rax, rax
.loop:
    cmp byte[rdi+rax], 0
    jz .end                     ; Leave if null-term is read
    inc rax
    jmp .loop
.end:
    ret

    ;; Принимает указатель на нуль-терминированную строку, выводит её в stdout
    ;; Указатель в rdi как первый аргумент
print_string:
    push rdi
    call string_length
    mov rdx, rax
    mov rax, WRITE_CALL
    mov rdi, STD_OUT
    pop rsi
    syscall
    ret

    ;; Переводит строку (выводит символ  с кодом 0xA)
print_newline:
    mov rdi, `\n`

    ;; Принимает код символа и выводит его в stdout
    ;; Код ASCII символа в rdi как первый аргумент
print_char:
    push di                     ;Pushing char because writing is done via buffer
                                ;Pushing 2 bytes because you can't push one (and rsp shenanigans would be slower)!
    mov rax, WRITE_CALL
    mov rsi, rsp
    mov rdx, STD_OUT
    mov rdi, 1
    syscall
    pop di
    ret

    ;; Выводит знаковое 8-байтовое число в десятичном формате
    ;; Число в rdi как первый аргумент
print_int:
    test rdi, rdi
    jns print_uint              ;Jump if int is positive
    neg rdi                     ;Transforming to sign-magnitude positive
    push rdi
    mov dil, '-'
    call print_char             ;Printing leading minus. I've spent an hour trying to come up with a cooler, cleaner solution, but I failed!
    pop rdi
                                ; Utilizing print_uint to print int
    ;; Выводит беззнаковое 8-байтовое число в десятичном формате
    ;; Совет: выделите место в стеке и храните там результаты деления
    ;; Не забудьте перевести цифры в их ASCII коды.
print_uint:
    mov rcx, UINT_MAX_DIGITS    ; rcx for keeping track of left space
    sub rsp, UINT_MAX_DIGITS+1  ; +1 for null-terminator
    mov rax, rdi
    mov rsi, DECIMAL_BASE       ; using rsi solely as operand in div
    mov byte[rsp+rcx], 0        ; null-terminating at the end
.loop:
    xor rdx, rdx
    div rsi
    dec rcx                     ; decreasing left space counter
    add dl, ASCII_DIGIT_OFFSET  ; ASCII-izing the digit
    mov byte[rsp+rcx], dl       ; writing digit(remainder) in the buffer
    test rax, rax
    jz .end                     ; leave if quotient is 0
    jnz .loop
.end:
    add rcx, rsp
    mov rdi, rcx                ; setting up buffer start
    call print_string
    add rsp, UINT_MAX_DIGITS+1  ; resetting rsp
    ret

    ;;Принимает два указателя на нуль-терминированные строки, возвращает 1 если они равны, 0 иначе
string_equals:
    xor rcx, rcx
.loop:
    mov al, byte[rdi+rcx]       ; rax is not cleared before usage, because only al is used
    cmp al, byte[rsi+rcx]
    jne .false                  ; false if bytes are different
    inc rcx
    test al, al                 ; leave if last byte was a null-term
    jnz .loop
.true:
    mov rax, 1
    ret
.false:
    xor rax, rax
    ret

    ;; Читает один символ из stdin и возвращает его. Возвращает 0 если достигнут конец потока
read_char:
    sub rsp, 2                  ;Allocating buffer
    mov rax, READ_CALL
    mov rdi, STD_IN
    mov rsi, rsp
    mov rdx, 1
    syscall
    test rax, rax
    jz .EOF                     ;leave if end of stream/file
    xor rax, rax
    pop ax                      ;retrieving char
    ret
.EOF:
    add rsp, 2                  ;resetting rsp
    ret

; Принимает: адрес начала буфера, размер буфера
; Читает в буфер слово из stdin, пропуская пробельные символы в начале, .
; Пробельные символы это пробел 0x20, табуляция 0x9 и перевод строки 0xA.
; Останавливается и возвращает 0 если слово слишком большое для буфера
; При успехе возвращает адрес буфера в rax, длину слова в rdx.
; При неудаче возвращает 0 в rax
; Эта функция должна дописывать к слову нуль-терминатор
read_word:
    push r12
    push r14
    mov r12, rdi                ;buffer adress
    xor r14, r14                ;read length
    test rsi, rsi
    jz .auto_success            ;jump to the end if the buffer length is 0
    push r13
    mov r13, rsi                ;buffer length
.white_skip:                    ;skipping leading whitespace crap
    call read_char
    cmp al, ' '
    je .white_skip
    cmp al, `\n`
    je .white_skip
    cmp al, `\t`
    je .white_skip
    jmp .null_check

.loop:
    cmp r13, 1
    jbe .fail                   ;fail if no place for null-term
    mov byte[r12+r14], al
    dec r13                     ;decreasing left buffer length
    inc r14                     ;counting read chars
    call read_char              ;reading a char
    cmp al, ' '
    je .success
    cmp al, `\n`
    je .success
    cmp al, `\t`
    je .success
.null_check:
    test rax, rax
    jz .success                 ;done reading if null-term is met
    jmp .loop
.success:
    mov byte[r12+r14], 0        ;null-terming the word
.auto_success:
    mov rax, r12
    mov rdx, r14
    jmp .end
.fail:
    mov rax, 0
.end:
    pop r13
    pop r14
    pop r12
    ret

; Принимает указатель на строку, пытается
; прочитать из её начала беззнаковое число.
; Возвращает в rax: число, rdx : его длину в символах
; rdx = 0 если число прочитать не удалось
parse_uint:
    xor r9, r9                  ;read chars count
    xor rax, rax                ;parsed num
    xor rsi, rsi                ;digit buffer
    mov rcx, DECIMAL_BASE
.loop:
    mov sil, [rdi+r9]           ;reading digit
    cmp sil, '0'
    jl .end
    cmp sil, '9'
    jg .end                     ;leaving if it's not an ASCII digit
    mul rcx                     ;*10 - decimal slide
    sub rsi, ASCII_DIGIT_OFFSET
    add rax, rsi                ;adding new digit to the rest
    inc r9
    cmp r9, UINT_MAX_DIGITS
    jge .end                    ;leave if max uint is already red
    jmp .loop
.end:
    mov rdx, r9
    ret

; Принимает указатель на строку, пытается
; прочитать из её начала знаковое число.
; Если есть знак, пробелы между ним и числом не разрешены.
; Возвращает в rax: число, rdx : его длину в символах (включая знак, если он был) 
; rdx = 0 если число прочитать не удалось
parse_int:
    mov sil, [rdi]
    cmp sil, '-'                ;skipping if char is not a minus
    jne .plus_check
    mov rsi, MIN_INT            ;both a sign symbol and a check for 2^63 val
    jmp .lead_sign
.plus_check:
    xor rsi, rsi                ;mov rsi, 0 but faster
    cmp sil, '+'
    jne .get_num                ;skipping if char is not a plus
.lead_sign:
    inc rdi                     ;incremting rdi for parse_uint not to read leading char
.get_num:
    push rsi
    call parse_uint             ;parsing rest of the num
    pop rsi                     ;retrieving sign
    test rdx, rdx
    jz .end                     ;leave if parsing failed
    test rax, rax               ;check if abs<=2^63-1
    jns .neg_check              ;skip if number is representable as signed int
    cmp rax, [rsp]              ;check if abs=2^63 and there was a leading minus
    je .neg_check
    mov rcx, DECIMAL_BASE       ;decimal slide to fit number in signed int
    div rcx
.neg_check:
    test rsi, rsi               ;check if parsed number is negative
    jns .end
    neg rax                     ;add sign to the number
    inc rdx                     ;as minus was skipped at the start of this func, increment total length here
.end:
    ret

; Принимает указатель на строку, указатель на буфер и длину буфера
; Копирует строку в буфер
; Возвращает длину строки если она умещается в буфер, иначе 0
string_copy:
    xor rax, rax
.loop:
    mov cl, [rdi+rax]
    cmp rax, rdx
    jae .fail                   ;fail if buffer is too small
    mov byte[rsi+rax], cl       ;move char to buffer
    inc rax
    test cl, cl
    jz .end                     ;leave if last char was a null-term
    jnz .loop
.fail:
    xor rax, rax
.end:
    ret
