    %assign i 0
    %macro iter 1

    mov rdx, %1_color_mask
    mov rcx, %1_mtrx_mask
    call reset_regs
    mov rdx, i
    %assign i i+1
    call do_filter

    %endmacro


    extern SEPIA_MTRX_TRNSPSD
    global PIXELS_PER_BATCH, filter_batch

    section .rodata

PIXELS_PER_BATCH: db 4
align 16
frst_color_mask:  db 0,1,2,3, 0,1,2,3, 0,1,2,3, 4,5,6,7
scnd_color_mask:  db 4,5,6,7, 4,5,6,7, 8,9,10,11, 8,9,10,11
thrd_color_mask:  db 8,9,10,11, 12,13,14,15, 12,13,14,15, 12,13,14,15
frst_mtrx_mask:   db 0,1,2,3, 4,5,6,7, 8,9,10,11, 0,1,2,3
scnd_mtrx_mask:   db 4,5,6,7, 8,9,10,11, 0,1,2,3, 4,5,6,7
thrd_mtrx_mask:   db 8,9,10,11, 0,1,2,3, 4,5,6,7, 8,9,10,11

    section .text
    ;; RDI holds address to the batch of float 3-item array
    ;; RSI holds address of pixel structures
filter_batch:
    ;; Loading default values to reduce memory fetches
    movdqa xmm6, [rdi]          ;blues
    movdqa xmm7, [rdi+16]       ;greens
    movdqa xmm8, [rdi+32]       ;reds

    movdqu xmm9, [SEPIA_MTRX_TRNSPSD]
    movdqu xmm10, [SEPIA_MTRX_TRNSPSD+12]
    movdqu xmm11, [SEPIA_MTRX_TRNSPSD+24]

    iter frst
    iter scnd
    iter thrd
    ret

    ;; Accepts color mask in rdx, mtrx mask in rcx
reset_regs:
    movdqa xmm0, xmm6
    pshufb xmm0, [rdx]

    movdqa xmm1, xmm7
    pshufb xmm1, [rdx]

    movdqa xmm2, xmm8
    pshufb xmm2, [rdx]

    movdqa xmm3, xmm9
    pshufb xmm3, [rcx]

    movdqa xmm4, xmm10
    pshufb xmm4, [rcx]

    movdqa xmm5, xmm11
    pshufb xmm5, [rcx]
    ret

    ;;Accepts iteration in rdx
do_filter:
    mulps xmm0, xmm3
    mulps xmm1, xmm4
    mulps xmm2, xmm5
    addps xmm0, xmm1
    addps xmm0, xmm2

    cvtps2dq xmm0, xmm0
    packssdw xmm0, xmm0
    packuswb xmm0, xmm0
    movd [rsi+4*rdx], xmm0
    ret
