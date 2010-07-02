;NSIS Modern User Interface - Language File
;Compatible with UltraModernUI 1.00 beta 2

;Language: Hungarian (1038) - Based on NSIS Official Hungarian Language
;By Tom Evin (evin@mailbox.hu)

;--------------------------------

!ifdef UMUI_MULTILANGUAGEPAGE
  ${LangFileString} UMUI_TEXT_MULTILANGUAGE_TITLE "�dv�zli a(z) $(^NameDA) Telep�t� Var�zsl�"
  ${LangFileString} UMUI_TEXT_MULTILANGUAGE_TEXT "A(z) $(^NameDA) telep�t�se el�tt, v�lasszon nyelvet:$\r$\n$\r$\n$_CLICK"
!endif

!ifdef UMUI_UNMULTILANGUAGEPAGE
  ${LangFileString} UMUI_UNTEXT_MULTILANGUAGE_TITLE "�dv�zli a(z) $(^NameDA) Elt�vol�t� Var�zsl�"
  ${LangFileString} UMUI_UNTEXT_MULTILANGUAGE_TEXT "A(z) $(^NameDA) elt�vol�t�sa el�tt, v�lasszon nyelvet:$\r$\n$\r$\n$_CLICK"
!endif

!ifdef UMUI_MULTILANGUAGEPAGE | UMUI_UNMULTILANGUAGEPAGE
  ${LangFileString} UMUI_TEXT_MULTILANGUAGE_LANGUAGE "Nyelv:"
!endif


!ifdef MUI_WELCOMEPAGE
  ${LangFileString} UMUI_TEXT_WELCOME_ALTERNATIVEINFO_TEXT "A var�zsl� v�gigvezeti a(z) $(^NameDA) telep�t�si folyamat�n.$\r$\n$\r$\n$\r$\n$_CLICK"
!endif


!ifdef UMUI_SERIALNUMBERPAGE | UMUI_UNSERIALNUMBERPAGE
  ${LangFileString} UMUI_TEXT_SERIALNUMBER_TITLE "Adja meg a(z) $(^NameDA) sorozatsz�m�t"
  ${LangFileString} UMUI_TEXT_SERIALNUMBER_SUBTITLE "K�rem t�ltse ki a k�vetkez� mez�ket."
  ${LangFileString} UMUI_TEXT_SERIALNUMBER_INFO_TEXT "K�rem t�ltse ki a k�vetkez� mez�ket. $_CLICK"
  ${LangFileString} UMUI_TEXT_SERIALNUMBER_INVALIDATE_TEXT "A(z) $UMUI_SNTEXT �rv�nytelen. Ellen�rizze �jra a megadott inform�ci�t."
  ${LangFileString} UMUI_TEXT_SERIALNUMBER_NAME "N�v"
  ${LangFileString} UMUI_TEXT_SERIALNUMBER_ORGANIZATION "Szervezet"
  ${LangFileString} UMUI_TEXT_SERIALNUMBER_SERIALNUMBER "Sorozatsz�m"
  ${LangFileString} UMUI_TEXT_SERIALNUMBER_ACTIVATIONCODE "Aktiv�ci�s k�d"
  ${LangFileString} UMUI_TEXT_SERIALNUMBER_PASSWORD "Jelsz�"
!endif 


!ifdef UMUI_CONFIRMPAGE
  ${LangFileString} UMUI_TEXT_INSTCONFIRM_SUBTITLE "A telep�t� begy�jt�tte az inform�ci�kat �s k�szen �ll a(z) $(^NameDA) telep�t�s�re."
  ${LangFileString} UMUI_TEXT_INSTCONFIRM_TITLE "Telep�t�s meger�s�t�se"
  ${LangFileString} UMUI_TEXT_INSTCONFIRM_TEXT_TOP "A telep�t� k�szen �ll a(z) $(^NameDA) telep�t�s�re.$\r$\nHa �t akarja n�zni vagy m�dos�tani a telep�t�si be�ll�t�sokat, kattintson a Vissza gombra. $_CLICK"
!endif 

!ifdef UMUI_UNCONFIRMPAGE
  ${LangFileString} UMUI_UNTEXT_INSTCONFIRM_SUBTITLE "A telep�t� begy�jt�tte az inform�ci�kat �s k�szen �ll a(z) $(^NameDA) elt�vol�t�s�ra."
  ${LangFileString} UMUI_UNTEXT_INSTCONFIRM_TITLE "Elt�vol�t�s meger�s�t�se"
  ${LangFileString} UMUI_UNTEXT_INSTCONFIRM_TEXT_TOP "A telep�t� k�szen �ll a(z) $(^NameDA) elt�vol�t�s�ra.$\r$\nHa �t akarja n�zni vagy m�dos�tani az elt�vol�t�si be�ll�t�sokat, kattintson a Vissza gombra. A Tov�bb gombbal elkezd�dik az elt�vol�t�s."
!endif 

!ifdef UMUI_CONFIRMPAGE | UMUI_UNCONFIRMPAGE
  ${LangFileString} UMUI_TEXT_INSTCONFIRM_TEXTBOX_TITLE "Aktu�lis be�ll�t�s:"
  ${LangFileString} UMUI_TEXT_INSTCONFIRM_TEXTBOX_DESTINATION_LOCATION "Telep�t�si hely:"
  ${LangFileString} UMUI_TEXT_INSTCONFIRM_TEXTBOX_START_MENU_FOLDER "Start men� mappa:"
  ${LangFileString} UMUI_TEXT_INSTCONFIRM_TEXTBOX_COMPNENTS "A k�vetkez� �sszetev�k lesznek telep�tve:"
!endif 


!ifdef UMUI_ABORTPAGE
  ${LangFileString} UMUI_TEXT_ABORT_INFO_TITLE "A(z) $(^NameDA) Telep�t� Var�zsl� befejez�d�tt"
  ${LangFileString} UMUI_TEXT_ABORT_INFO_TEXT "A var�zsl� megszakadt, miel�tt a(z) $(^NameDA) sikeresen telep�t�sre ker�lt volna.$\r$\n$\r$\nA program k�s�bbi telep�t�s�hez, futtassa �jra a telep�t�t.$\r$\n$\r$\n$\r$\n$\r$\nA $(^CloseBtn) gombbal kil�phet a Telep�t� Var�zsl�b�l."
!endif 

!ifdef UMUI_UNABORTPAGE
  ${LangFileString} UMUI_UNTEXT_ABORT_INFO_TITLE "A(z) $(^NameDA) Elt�vol�t�s Var�zsl� befejez�d�tt"
  ${LangFileString} UMUI_UNTEXT_ABORT_INFO_TEXT "A var�zsl� megszakadt, miel�tt a(z) $(^NameDA) sikeresen elt�vol�t�sra ker�lt volna.$\r$\n$\r$\nA program k�s�bbi elt�vol�t�s�hoz, futtassa �jra az elt�vol�t�t.$\r$\n$\r$\n$\r$\n$\r$\nA $(^CloseBtn) gombbal kil�phet az Elt�vol�t�s Var�zsl�b�l."
!endif


!ifdef UMUI_SETUPTYPEPAGE
  ${LangFileString} UMUI_TEXT_SETUPTYPE_TITLE "Telep�t�si t�pus"
  ${LangFileString} UMUI_TEXT_SETUPTYPE_SUBTITLE "V�lassza ki a k�v�nt telep�t�si t�pust."
  ${LangFileString} UMUI_TEXT_SETUPTYPE_INFO_TEXT "V�lasszon egy telep�t�si t�pust."
  ${LangFileString} UMUI_TEXT_SETUPTYPE_MINIMAL_TEXT "Csak a sz�ks�ges szolg�ltat�sok lesznek telep�tve. (Kevesebb lemezter�let sz�ks�ges)"
  ${LangFileString} UMUI_TEXT_SETUPTYPE_STANDARD_TEXT "Minden f� szolg�ltat�s telep�tve lesz. A legt�bb felhaszn�l�nak aj�nlott."
  ${LangFileString} UMUI_TEXT_SETUPTYPE_COMPLETE_TEXT "Minden szolg�ltat�s telep�tve lesz. (A legt�bb lemezter�let sz�ks�ges)"
  ${LangFileString} UMUI_TEXT_SETUPTYPE_CUSTOM_TEXT "Kiv�laszthat� mely program szolg�ltat�sok telep�ljenek �s hova. Halad� felhaszn�l�knak aj�nlott."
!endif 

!ifdef UMUI_UNSETUPTYPEPAGE
  ${LangFileString} UMUI_UNTEXT_SETUPTYPE_TITLE "Elt�vol�t�si t�pus"
  ${LangFileString} UMUI_UNTEXT_SETUPTYPE_SUBTITLE "V�lassza ki a k�v�nt elt�vol�t�si t�pust."
  ${LangFileString} UMUI_UNTEXT_SETUPTYPE_INFO_TEXT "V�lasszon egy elt�vol�t�si t�pust."
  ${LangFileString} UMUI_UNTEXT_SETUPTYPE_MINIMAL_TEXT "Csak a f� szolg�ltat�sok maradnak meg."
  ${LangFileString} UMUI_UNTEXT_SETUPTYPE_STANDARD_TEXT "Csak a sz�ks�ges szolg�ltat�sok maradnak meg."
  ${LangFileString} UMUI_UNTEXT_SETUPTYPE_COMPLETE_TEXT "Minden program szolg�ltat�s el lesz t�vol�tva."
  ${LangFileString} UMUI_UNTEXT_SETUPTYPE_CUSTOM_TEXT "Kiv�laszthat� mely program szolg�ltat�sok ker�ljenek elt�vol�t�sra."
!endif 

!ifdef UMUI_SETUPTYPEPAGE | UMUI_UNSETUPTYPEPAGE
  ${LangFileString} UMUI_TEXT_SETUPTYPE_MINIMAL_TITLE "Minim�lis"
  ${LangFileString} UMUI_TEXT_SETUPTYPE_STANDARD_TITLE "�ltal�nos"
  ${LangFileString} UMUI_TEXT_SETUPTYPE_COMPLETE_TITLE "Teljes"
  ${LangFileString} UMUI_TEXT_SETUPTYPE_CUSTOM_TITLE "Egy�ni"
!endif 


!ifdef UMUI_INFORMATIONPAGE
  ${LangFileString} UMUI_TEXT_INFORMATION_SUBTITLE "Olvassa el a(z) $(^NameDA) telep�t�s�re vonatkoz� inform�ci�kat."
!endif 

!ifdef UMUI_UNINFORMATIONPAGE
  ${LangFileString} UMUI_UNTEXT_INFORMATION_SUBTITLE "Olvassa el a(z) $(^NameDA) elt�vol�t�s�ra vonatkoz� inform�ci�kat."
!endif 

!ifdef UMUI_INFORMATIONPAGE | UMUI_UNINFORMATIONPAGE
  ${LangFileString} UMUI_TEXT_INFORMATION_TITLE "Inform�ci�"
  ${LangFileString} UMUI_TEXT_INFORMATION_INFO_TEXT "A(z) $(^NameDA) kapcsol�d� inform�ci�i."
!endif 


!ifdef UMUI_ADDITIONALTASKSPAGE
  ${LangFileString} UMUI_TEXT_ADDITIONALTASKS_INFO_TEXT "V�lasszon tov�bbi feladatot, melyet a telep�t� v�grehajt a(z) $(^NameDA) telep�t�se sor�n. $_CLICK"
!endif 

!ifdef UMUI_UNADDITIONALTASKSPAGE
  ${LangFileString} UMUI_UNTEXT_ADDITIONALTASKS_INFO_TEXT "V�lasszon tov�bbi feladatot, melyet a telep�t� v�grehajt a(z) $(^NameDA) elt�vol�t�sa sor�n. $_CLICK"
!endif 

!ifdef UMUI_ADDITIONALTASKSPAGE | UMUI_UNADDITIONALTASKSPAGE
  ${LangFileString} UMUI_TEXT_ADDITIONALTASKS_TITLE "Tov�bbi feladatok"
  ${LangFileString} UMUI_TEXT_ADDITIONALTASKS_SUBTITLE "Mely tov�bbi feladatot kell v�grehajtani?"
  ${LangFileString} UMUI_TEXT_ADDITIONALTASKS_ADDITIONAL_ICONS "Tov�bbi ikonok:"
  ${LangFileString} UMUI_TEXT_ADDITIONALTASKS_CREATE_DESKTOP_ICON "Asztali ikon l�trehoz�sa"
  ${LangFileString} UMUI_TEXT_ADDITIONALTASKS_CREATE_QUICK_LAUNCH_ICON "Gyorsind�t� ikon l�trehoz�sa"
  ${LangFileString} UMUI_TEXT_ADDITIONALTASKS_ADVANCED_PARAMETERS "Tov�bbi param�terek:"
  ${LangFileString} UMUI_TEXT_ADDITIONALTASKS_LAUNCH_PROGRAM_AT_WINDOWS_STARTUP "A(z) $(^NameDA) ind�t�sa a Windows-al"
  ${LangFileString} UMUI_TEXT_ADDITIONALTASKS_FILE_ASSOCIATION "F�jl t�rs�t�sa:"
  ${LangFileString} UMUI_TEXT_ADDITIONALTASKS_ASSOCIATE_WITH "$(^NameDA) t�rs�t�sa a(z) "
  ${LangFileString} UMUI_TEXT_ADDITIONALTASKS_ASSOCIATE_WITH_END " f�jlt�pussal"
!endif 
  
  
!ifdef UMUI_CONFIRMPAGE | UMUI_UNCONFIRMPAGE | UMUI_ALTERNATIVESTARTMENUPAGE | UMUI_UNALTERNATIVESTARTMENUPAGE
  ${LangFileString} UMUI_TEXT_SHELL_VAR_CONTEXT "Parancsikonok l�trehoz�sa:"
  ${LangFileString} UMUI_TEXT_SHELL_VAR_CONTEXT_FOR_ALL_USERS "Minden felhaszn�l�nak"
  ${LangFileString} UMUI_TEXT_SHELL_VAR_CONTEXT_ONLY_FOR_CURRENT_USER "Csak az aktu�lis felhaszn�l�nak"
!endif


!ifdef UMUI_UPDATEPAGE
  ${LangFileString} UMUI_TEXT_UPDATE_TITLE "Friss�t�s"
  ${LangFileString} UMUI_TEXT_UPDATE_SUBTITLE "A program kor�bbi verzi�j�nak friss�t�se."
  ${LangFileString} UMUI_TEXT_UPDATE_INFO_TEXT "�dv�zli a(z) $(^NameDA) friss�t� var�zsl�.$\r$\nEz a program friss�ti a sz�m�t�g�pen tal�lhat� $OLDVERSION verzi�t."
  ${LangFileString} UMUI_TEXT_UPDATE_UPDATE_TITLE "Friss�t�s"
  ${LangFileString} UMUI_TEXT_UPDATE_UPDATE_TEXT "A(z) $(^NameDA) minden, m�r telep�tett elem�nek friss�t�se $NEWVERSION verzi�ra."
  ${LangFileString} UMUI_TEXT_UPDATE_REMOVE_TITLE "Elt�vol�t�s"
  ${LangFileString} UMUI_TEXT_UPDATE_REMOVE_TEXT "A(z) $(^NameDA) elt�vol�t�sa a sz�m�t�g�pr�l."
  ${LangFileString} UMUI_TEXT_UPDATE_CONTINUE_TITLE "Telep�t�s folytat�sa"
  ${LangFileString} UMUI_TEXT_UPDATE_CONTINUE_TEXT "Telep�t�s folytat�sa a megszokott m�don. Ez az opci� akkor aj�nlott, ha egy l�tez� telep�t�sre akarja �jratelep�teni a programot vagy ez�ttal egy m�sik mapp�ba telep�ten�."
!endif


!ifdef UMUI_MAINTENANCEPAGE | UMUI_UNMAINTENANCEPAGE
  ${LangFileString} UMUI_TEXT_MAINTENANCE_TITLE "Karbantart�s"
  ${LangFileString} UMUI_TEXT_MAINTENANCE_SUBTITLE "A program m�dos�t�sa, jav�t�sa vagy elt�vol�t�sa."
  ${LangFileString} UMUI_TEXT_MAINTENANCE_INFO_TEXT "�dv�zli a(z) $(^NameDA) telep�t� karbantart� programja.$\r$\nA programmal m�dos�thatja az aktu�lis telep�t�st."
  ${LangFileString} UMUI_TEXT_MAINTENANCE_MODIFY_TITLE "M�dos�t�s"
  ${LangFileString} UMUI_TEXT_MAINTENANCE_MODIFY_TEXT "�j �sszetev�k telep�thet�k fel vagy m�r telep�tettek t�vol�that�k el."
  ${LangFileString} UMUI_TEXT_MAINTENANCE_REPAIR_TITLE "Jav�t�s"
  ${LangFileString} UMUI_TEXT_MAINTENANCE_REPAIR_TEXT "A(z) $(^NameDA) minden telep�tett �sszetev�j�nek �jratelep�t�se."
  ${LangFileString} UMUI_TEXT_MAINTENANCE_REMOVE_TITLE "Elt�vol�t�s"
  ${LangFileString} UMUI_TEXT_MAINTENANCE_REMOVE_TEXT "A(z) $(^NameDA) elt�vol�t�sa a sz�m�t�g�pr�l."
  ${LangFileString} UMUI_TEXT_MAINTENANCE_CONTINUE_TITLE "Telep�t�s folytat�sa"
  ${LangFileString} UMUI_TEXT_MAINTENANCE_CONTINUE_TEXT "Telep�t�s folytat�sa a megszokott m�don. Ez az opci� akkor aj�nlott, ha egy l�tez� telep�t�sre akarja �jratelep�teni a programot vagy ez�ttal egy m�sik mapp�ba telep�ten�."
!endif


!ifdef UMUI_FILEDISKREQUESTPAGE | UMUI_UNFILEDISKREQUESTPAGE
  ${LangFileString} UMUI_TEXT_FILEDISKREQUEST_FILE_SUBTITLE_BEGIN "A telep�t�shez sz�ks�ges a(z)"
  ${LangFileString} UMUI_TEXT_FILEDISKREQUEST_FILE_SUBTITLE_END "f�jl a folytat�shoz."
  ${LangFileString} UMUI_TEXT_FILEDISKREQUEST_DISK_SUBTITLE "A telep�t�shez sz�ks�ges a k�vetkez� lemez a folytat�shoz."
  ${LangFileString} UMUI_TEXT_FILEDISKREQUEST_FILE_BEGIN "Adja meg a f�jl el�r�s�t"
  ${LangFileString} UMUI_TEXT_FILEDISKREQUEST_FILE_END "a folytat�shoz."
  ${LangFileString} UMUI_TEXT_FILEDISKREQUEST_DISK "Helyezze be:"
  ${LangFileString} UMUI_TEXT_FILEDISKREQUEST_PATH "El�r�s:"
!endif
