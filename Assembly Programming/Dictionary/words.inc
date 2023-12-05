    %include "colon.inc"
    section .data
    ;; Different ways of referencing keys and values are used, see colon.inc for ref.
dontsova:
    db  "D. Dontsova", 0
long_name:
    times 255 db "A"
    db 0
too_long_name:
    times 256 db "B"
    db 0

    colon "G. R. Martin", title1
    db "The Song of Ice and Fire Antology", 0

    colon dontsova, "Kulinarnaya kniga lentyaiki"

    colon "I. Zhirkov", title2
    db "Low Level Programming", 0

    colon "L. Tolstoy", "The War and Peace Vol 1-4"

    colon `\t`, "Tabularity and Singularity"
    colon `\n\r\n\r`, "The Whitespace"

    colon long_name, "Long Novel"
    colon too_long_name, "Silent Don"
