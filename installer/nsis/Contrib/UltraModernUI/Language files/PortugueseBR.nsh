;NSIS Modern User Interface - Language File
;Compatible with UltraModernUI 1.00 beta 2

;Language: Brazilian Portuguese (1046)
;By Jenner Modesto <jennermodesto@gmail.com>

;--------------------------------

!ifdef UMUI_CONFIRMPAGE
  ${LangFileString} UMUI_TEXT_INSTCONFIRM_SUBTITLE "O Assistente terminou de reunir informa��es e est� pronto para instalar $(^NameDA)."
  ${LangFileString} UMUI_TEXT_INSTCONFIRM_TITLE "Confirmar a Instala��o"
  ${LangFileString} UMUI_TEXT_INSTCONFIRM_TEXT_TOP "O Assistente est� pronto para instalar $(^NameDA) em seu computador.$\r$\nSe quiser rever ou mudar quaisquer configura��es da instala��o, clique em Voltar. $_CLICK"
!endif 

!ifdef UMUI_UNCONFIRMPAGE
  ${LangFileString} UMUI_UNTEXT_INSTCONFIRM_SUBTITLE "O Assistente terminou de reunir informa��es e est� pronto para instalar $(^NameDA)."
  ${LangFileString} UMUI_UNTEXT_INSTCONFIRM_TITLE "Confirmar a desinstala��o"
  ${LangFileString} UMUI_UNTEXT_INSTCONFIRM_TEXT_TOP "O Assistente est� pronto para desinstalar $(^NameDA) em seu computador.$\r$\nSe quiser rever ou mudar quaisquer configura��es da desinstala��o, clique em Voltar. Clique em Avan�ar para iniciar a desinstala��o."
!endif 

!ifdef UMUI_CONFIRMPAGE | UMUI_UNCONFIRMPAGE
  ${LangFileString} UMUI_TEXT_INSTCONFIRM_TEXTBOX_TITLE "Configura��o atual:"
  ${LangFileString} UMUI_TEXT_INSTCONFIRM_TEXTBOX_DESTINATION_LOCATION "Diret�rio de instala��o:"
  ${LangFileString} UMUI_TEXT_INSTCONFIRM_TEXTBOX_START_MENU_FOLDER "Menu iniciar:"
!endif 


!ifdef UMUI_ABORTPAGE
  ${LangFileString} UMUI_TEXT_ABORT_INFO_TITLE "Terminando o Assistente de Instala��o de $(^NameDA)"
  ${LangFileString} UMUI_TEXT_ABORT_INFO_TEXT "O Assistente foi interrompido antes que $(^Name) pudesse ser totalmente instalado.$\r$\n$\r$\nPara instalar esse programa mais tarde, execute o assistente novamente.$\r$\n$\r$\n$\r$\n$\r$\nClique em $(^CloseBtn) para sair do Assistente."
!endif 

!ifdef UMUI_UNABORTPAGE
  ${LangFileString} UMUI_UNTEXT_ABORT_INFO_TITLE "Terminando o Assistente de Desinstala��o de $(^NameDA)"
  ${LangFileString} UMUI_UNTEXT_ABORT_INFO_TEXT "O Assistente foi interrompido antes que $(^Name) pudesse ser totalmente desinstalado.$\r$\n$\r$\nPara desinstalar esse programa mais tarde, execute o assistente novamente.$\r$\n$\r$\n$\r$\n$\r$\nClique em $(^CloseBtn) para sair do Assistente."
!endif 
