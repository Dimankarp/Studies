    %define last_ref 0x0
    %macro colon 2

    %ifstr %1
%%key_str:
        db %1, 0
    %endif

    %ifstr %2
%%val_str:
        db %2, 0
    %endif
%%item:
    dq last_ref
    %ifstr %1
        dq %%key_str
    %else
        %ifid %1
            dq %1
        %else
            %error "Key must either be a string or a label to one."
        %endif
    %endif

    %ifstr %2
        dq %%val_str
    %else
        %ifid %2
            dq %2
            %2:
        %else
            %error "Value must either be a string or a label."
        %endif
    %endif
    %define last_ref %%item
    %endmacro
