;NSIS Modern User Interface - Language File
;Compatible with UltraModernUI 1.00 beta 2

;Language: Brazilian Portuguese (1031)
;By Tobias <tm2006@users.sourceforge.net>

;--------------------------------

!ifdef UMUI_CONFIRMPAGE
  ${LangFileString} UMUI_TEXT_INSTCONFIRM_SUBTITLE "Setup hat die ben�tigten Informationen gesammelt und ist bereit, $(^NameDA) zu installieren."
  ${LangFileString} UMUI_TEXT_INSTCONFIRM_TITLE "Installation best�tigen"
  ${LangFileString} UMUI_TEXT_INSTCONFIRM_TEXT_TOP "Setup ist bereit, $(^NameDA) auf Ihrem Computer zu installieren.$\r$\nFalls Sie Ihre Installationseinstellungen noch �berpr�fen oder �ndern m�chten, klicken Sie auf Zur�ck. Klicken Sie auf Weiter, um die Installation zu beginnen. $_CLICK"
!endif 

!ifdef UMUI_UNCONFIRMPAGE
  ${LangFileString} UMUI_UNTEXT_INSTCONFIRM_SUBTITLE "O Assistente terminou de reunir informa��es e est� pronto para instalar $(^NameDA)."
  ${LangFileString} UMUI_UNTEXT_INSTCONFIRM_TITLE "Confirmar a desinstala��o"
  ${LangFileString} UMUI_UNTEXT_INSTCONFIRM_TEXT_TOP "O Assistente est� pronto para desinstalar $(^NameDA) em seu computador.$\r$\nSe quiser rever ou mudar quaisquer configura��es da desinstala��o, clique em Voltar. Clique em Avan�ar para iniciar a desinstala��o."
!endif 

!ifdef UMUI_CONFIRMPAGE | UMUI_UNCONFIRMPAGE
  ${LangFileString} UMUI_TEXT_INSTCONFIRM_TEXTBOX_TITLE "Derzeitige konfiguration:"
  ${LangFileString} UMUI_TEXT_INSTCONFIRM_TEXTBOX_DESTINATION_LOCATION "Zielort:"
  ${LangFileString} UMUI_TEXT_INSTCONFIRM_TEXTBOX_START_MENU_FOLDER "Startmen�-ordner:"
!endif 


!ifdef UMUI_ABORTPAGE
  ${LangFileString} UMUI_TEXT_ABORT_INFO_TITLE "Beenden des $(^NameDA) Setup-Assistenten"
  ${LangFileString} UMUI_TEXT_ABORT_INFO_TEXT "Der Assistent wurde unterbrochen, bevor $(^NameDA) komplett installiert werden konnte.$\r$\n$\r$\nUm das Programm zu einem sp�teren Zeitpunkt zu installieren, f�hren Sie dieses Setup bitte erneut aus.$\r$\n$\r$\n$\r$\n$\r$\nKlicken Sie auf $(^CloseBtn), um den Installationsassistenten zu schlie�en."
!endif 

!ifdef UMUI_UNABORTPAGE
  ${LangFileString} UMUI_UNTEXT_ABORT_INFO_TITLE "Beenden des Deinstallationsassistenten f�r $(^NameDA)"
  ${LangFileString} UMUI_UNTEXT_ABORT_INFO_TEXT "Der Assistent wurde unterbrochen, bevor $(^NameDA) komplett deinstalliert werden konnte.$\r$\n$\r$\nUm das Programm zu einem sp�teren Zeitpunkt zu deinstallieren, f�hren Sie dieses Setup bitte erneut aus.$\r$\n$\r$\n$\r$\n$\r$\nKlicken Sie auf $(^CloseBtn), um den Deinstallationsassistenten zu schlie�en."
!endif 
