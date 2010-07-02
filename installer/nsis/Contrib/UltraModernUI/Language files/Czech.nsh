;NSIS Modern User Interface - Language File
;Compatible with UltraModernUI 1.00 beta 2

;Language: Czech (1029)
;By Pospec (pospec4444atgmaildotcom)

;--------------------------------

!ifdef UMUI_MULTILANGUAGEPAGE
  ${LangFileString} UMUI_TEXT_MULTILANGUAGE_TITLE "Vítejte v prùvodci instalací $(^NameDA)"
  ${LangFileString} UMUI_TEXT_MULTILANGUAGE_TEXT "Pøed zaèátkem instalace $(^NameDA) vyberte, prosím, jazyk:$\r$\n$\r$\n$_CLICK"
!endif

!ifdef UMUI_UNMULTILANGUAGEPAGE
  ${LangFileString} UMUI_UNTEXT_MULTILANGUAGE_TITLE "Vítejte v prùvodci odebráním $(^NameDA)"
  ${LangFileString} UMUI_UNTEXT_MULTILANGUAGE_TEXT "Pøed zaèátkem odinstalace $(^NameDA) vyberte, prosím, jazyk:$\r$\n$\r$\n$_CLICK"
!endif

!ifdef UMUI_MULTILANGUAGEPAGE | UMUI_UNMULTILANGUAGEPAGE
  ${LangFileString} UMUI_TEXT_MULTILANGUAGE_LANGUAGE "Jazyk:"
!endif


!ifdef MUI_WELCOMEPAGE
  ${LangFileString} UMUI_TEXT_WELCOME_ALTERNATIVEINFO_TEXT "Tento prùvodce vás provede instalací $(^NameDA).$\r$\n$\r$\n$\r$\n$_CLICK"
!endif


!ifdef UMUI_SERIALNUMBERPAGE | UMUI_UNSERIALNUMBERPAGE
  ${LangFileString} UMUI_TEXT_SERIALNUMBER_TITLE "Zadejte sériové èíslo pro $(^NameDA)"
  ${LangFileString} UMUI_TEXT_SERIALNUMBER_SUBTITLE "Vyplòte pole níe, prosím."
  ${LangFileString} UMUI_TEXT_SERIALNUMBER_INFO_TEXT "Vyplòte pole níe, prosím. $_CLICK"
  ${LangFileString} UMUI_TEXT_SERIALNUMBER_INVALIDATE_TEXT "$UMUI_SNTEXT je nesprávné. Zkontrolujte, prosím, zda jste zadali správné údaje."
  ${LangFileString} UMUI_TEXT_SERIALNUMBER_NAME "Jméno"
  ${LangFileString} UMUI_TEXT_SERIALNUMBER_ORGANIZATION "Organizace"
  ${LangFileString} UMUI_TEXT_SERIALNUMBER_SERIALNUMBER "Sériové èíslo"
  ${LangFileString} UMUI_TEXT_SERIALNUMBER_ACTIVATIONCODE "Aktivaèní kód"
  ${LangFileString} UMUI_TEXT_SERIALNUMBER_PASSWORD "Heslo"
!endif 


!ifdef UMUI_CONFIRMPAGE
  ${LangFileString} UMUI_TEXT_INSTCONFIRM_SUBTITLE "Prùvodce získal všechny potøebné informace a je pøipraven nainstalovat produkt $(^NameDA)."
  ${LangFileString} UMUI_TEXT_INSTCONFIRM_TITLE "Potvrdit instalaci"
  ${LangFileString} UMUI_TEXT_INSTCONFIRM_TEXT_TOP "Prùvodce je pøipraven nainstalovat $(^NameDA) na váš poèítaè.$\r$\nPokud chcete zkontrolovat nebo zmìnit nìkteré parametry instalace, kliknìte na Zpìt. $_CLICK"
!endif 

!ifdef UMUI_UNCONFIRMPAGE
  ${LangFileString} UMUI_UNTEXT_INSTCONFIRM_SUBTITLE "Prùvodce získal všechny potøebné informace a je pøipraven odebrat produkt $(^NameDA)."
  ${LangFileString} UMUI_UNTEXT_INSTCONFIRM_TITLE "Potvrdit odebrání"
  ${LangFileString} UMUI_UNTEXT_INSTCONFIRM_TEXT_TOP "Prùvodce je pøipraven odebrat $(^NameDA) na váš poèítaè.$\r$\nPokud chcete zkontrolovat nebo zmìnit nìkteré parametry odebrat, kliknìte na Zpìt. Kliknìte na Další pro odebrání."
!endif 

!ifdef UMUI_CONFIRMPAGE | UMUI_UNCONFIRMPAGE
  ${LangFileString} UMUI_TEXT_INSTCONFIRM_TEXTBOX_TITLE "Stávající nastavení:"
  ${LangFileString} UMUI_TEXT_INSTCONFIRM_TEXTBOX_DESTINATION_LOCATION "Cílová sloka:"
  ${LangFileString} UMUI_TEXT_INSTCONFIRM_TEXTBOX_START_MENU_FOLDER "Nabídka Start:"
  ${LangFileString} UMUI_TEXT_INSTCONFIRM_TEXTBOX_COMPNENTS "Budou instalovány následující komponenty:"
!endif 


!ifdef UMUI_ABORTPAGE
  ${LangFileString} UMUI_TEXT_ABORT_INFO_TITLE "Dokonèování prùvodce instalací $(^NameDA)"
  ${LangFileString} UMUI_TEXT_ABORT_INFO_TEXT "Prùvodce instalací byl ukonèen døíve, ne mohl bıt $(^NameDA) kompletnì nainstalovanı.$\r$\n$\r$\nPro pozdìjší instalace spuste, prosím, instalátor znovu.$\r$\n$\r$\n$\r$\n$\r$\nKliknìte na $(^CloseBtn) pro ukonèení prùvodce instalací."
!endif 

!ifdef UMUI_UNABORTPAGE
  ${LangFileString} UMUI_UNTEXT_ABORT_INFO_TITLE "Dokonèování prùvodce odebráním $(^NameDA)"
  ${LangFileString} UMUI_UNTEXT_ABORT_INFO_TEXT "Prùvodce odebráním byl ukonèen døíve, ne mohl bıt $(^NameDA) kompletnì odebrán.$\r$\n$\r$\nPro pozdìjší odebrání spuste, prosím, tohoto prùvodce znovu.$\r$\n$\r$\n$\r$\n$\r$\nKliknìte na $(^CloseBtn) pro ukonèení prùvodce odebráním."
!endif


!ifdef UMUI_SETUPTYPEPAGE
  ${LangFileString} UMUI_TEXT_SETUPTYPE_TITLE "Typ instalace"
  ${LangFileString} UMUI_TEXT_SETUPTYPE_SUBTITLE "Prosím, vyberte typ instalace, kterı vám vyhovuje."
  ${LangFileString} UMUI_TEXT_SETUPTYPE_INFO_TEXT "Prosím, vyberte typ instalace."
  ${LangFileString} UMUI_TEXT_SETUPTYPE_MINIMAL_TEXT "Budou instalovány jen nutné souèásti. (Šetøí místo na disku)"
  ${LangFileString} UMUI_TEXT_SETUPTYPE_STANDARD_TEXT "Budou se instalovat všechny dùleité souèásti. Doporuèuje se pro vìtšinu uivatelù."
  ${LangFileString} UMUI_TEXT_SETUPTYPE_COMPLETE_TEXT "Budou se instalovat všechny souèásti. (Potøebuje nejvíce místa na disku)"
  ${LangFileString} UMUI_TEXT_SETUPTYPE_CUSTOM_TEXT "Vyberte souèásti, které se budou instalovat a urèete, kam se nainstalují. Doporuèuje se pro zkušené uivatele."
!endif 

!ifdef UMUI_UNSETUPTYPEPAGE
  ${LangFileString} UMUI_UNTEXT_SETUPTYPE_TITLE "Typ odinstalace"
  ${LangFileString} UMUI_UNTEXT_SETUPTYPE_SUBTITLE "Prosím, vyberte typ odinstalace, kterı vám vyhovuje."
  ${LangFileString} UMUI_UNTEXT_SETUPTYPE_INFO_TEXT "Prosím, vyberte typ odinstalace."
  ${LangFileString} UMUI_UNTEXT_SETUPTYPE_MINIMAL_TEXT "Bude ponechána vìtšina souèástí."
  ${LangFileString} UMUI_UNTEXT_SETUPTYPE_STANDARD_TEXT "Budou ponechány nutné souèástí."
  ${LangFileString} UMUI_UNTEXT_SETUPTYPE_COMPLETE_TEXT "Celá aplikace bude odstranìna."
  ${LangFileString} UMUI_UNTEXT_SETUPTYPE_CUSTOM_TEXT "Vyberte souèásti programu, které se odeberou."
!endif 

!ifdef UMUI_SETUPTYPEPAGE | UMUI_UNSETUPTYPEPAGE
  ${LangFileString} UMUI_TEXT_SETUPTYPE_MINIMAL_TITLE "Minimální"
  ${LangFileString} UMUI_TEXT_SETUPTYPE_STANDARD_TITLE "Standardní"
  ${LangFileString} UMUI_TEXT_SETUPTYPE_COMPLETE_TITLE "Úplná"
  ${LangFileString} UMUI_TEXT_SETUPTYPE_CUSTOM_TITLE "Vlastní"
!endif 


!ifdef UMUI_INFORMATIONPAGE
  ${LangFileString} UMUI_TEXT_INFORMATION_SUBTITLE "Prosím, vezmìte v úvahu tyto informace tıkající se instalace $(^NameDA)."
!endif 

!ifdef UMUI_UNINFORMATIONPAGE
  ${LangFileString} UMUI_UNTEXT_INFORMATION_SUBTITLE "Prosím, vezmìte v úvahu tyto informace tıkající se odebrání $(^NameDA)."
!endif 

!ifdef UMUI_INFORMATIONPAGE | UMUI_UNINFORMATIONPAGE
  ${LangFileString} UMUI_TEXT_INFORMATION_TITLE "Informace"
  ${LangFileString} UMUI_TEXT_INFORMATION_INFO_TEXT "Informace tıkající se $(^NameDA)."
!endif 


!ifdef UMUI_ADDITIONALTASKSPAGE
  ${LangFileString} UMUI_TEXT_ADDITIONALTASKS_INFO_TEXT "Vyberte dodateèné akce, které má prùvodce provést v prùbìhu instalace $(^NameDA). $_CLICK"
!endif 

!ifdef UMUI_UNADDITIONALTASKSPAGE
  ${LangFileString} UMUI_UNTEXT_ADDITIONALTASKS_INFO_TEXT "Vyberte dodateèné akce, které má prùvodce provést v prùbìhu odebrání $(^NameDA). $_CLICK"
!endif 

!ifdef UMUI_ADDITIONALTASKSPAGE | UMUI_UNADDITIONALTASKSPAGE
  ${LangFileString} UMUI_TEXT_ADDITIONALTASKS_TITLE "Dodateèné akce"
  ${LangFileString} UMUI_TEXT_ADDITIONALTASKS_SUBTITLE "Které dodateèné akce chcete provést?"
  ${LangFileString} UMUI_TEXT_ADDITIONALTASKS_ADDITIONAL_ICONS "Ikony:"
  ${LangFileString} UMUI_TEXT_ADDITIONALTASKS_CREATE_DESKTOP_ICON "Vytvoøit ikonu na ploše"
  ${LangFileString} UMUI_TEXT_ADDITIONALTASKS_CREATE_QUICK_LAUNCH_ICON "Vytvoøit ikonu snadného spuštìní"
  ${LangFileString} UMUI_TEXT_ADDITIONALTASKS_ADVANCED_PARAMETERS "Dalsi parametry:"
  ${LangFileString} UMUI_TEXT_ADDITIONALTASKS_LAUNCH_PROGRAM_AT_WINDOWS_STARTUP "Spouštìt $(^NameDA) po startu Windows"
  ${LangFileString} UMUI_TEXT_ADDITIONALTASKS_FILE_ASSOCIATION "Asociace souborù:"
  ${LangFileString} UMUI_TEXT_ADDITIONALTASKS_ASSOCIATE_WITH "Asociovat $(^NameDA) s "
  ${LangFileString} UMUI_TEXT_ADDITIONALTASKS_ASSOCIATE_WITH_END " soubory"
!endif 
  
  
!ifdef UMUI_CONFIRMPAGE | UMUI_UNCONFIRMPAGE | UMUI_ALTERNATIVESTARTMENUPAGE | UMUI_UNALTERNATIVESTARTMENUPAGE
  ${LangFileString} UMUI_TEXT_SHELL_VAR_CONTEXT "Vytvoøit zástupce pro:"
  ${LangFileString} UMUI_TEXT_SHELL_VAR_CONTEXT_FOR_ALL_USERS "Všechny uivatele"
  ${LangFileString} UMUI_TEXT_SHELL_VAR_CONTEXT_ONLY_FOR_CURRENT_USER "Jen pro mì"
!endif


!ifdef UMUI_UPDATEPAGE
  ${LangFileString} UMUI_TEXT_UPDATE_TITLE "Aktualizovat"
  ${LangFileString} UMUI_TEXT_UPDATE_SUBTITLE "Aktualizace pøedchozí verze programu."
  ${LangFileString} UMUI_TEXT_UPDATE_INFO_TEXT "Vítejte v prùvodci aktualizací $(^NameDA) .$\r$\nTento prùvodce umoòuje aktualizovat $OLDVERSION nalezenou na vašem poèítaèi."
  ${LangFileString} UMUI_TEXT_UPDATE_UPDATE_TITLE "Aktualizovat"
  ${LangFileString} UMUI_TEXT_UPDATE_UPDATE_TEXT "Aktualizovat všechny souèásti $(^NameDA) nainstalované pøed verzí $NEWVERSION.."
  ${LangFileString} UMUI_TEXT_UPDATE_REMOVE_TITLE "Odebrat"
  ${LangFileString} UMUI_TEXT_UPDATE_REMOVE_TEXT "Odebrat $(^NameDA) z vašeho poèítaèe."
  ${LangFileString} UMUI_TEXT_UPDATE_CONTINUE_TITLE "Pokraèovat v instalaci"
  ${LangFileString} UMUI_TEXT_UPDATE_CONTINUE_TEXT "Pokraèovat v instalaci obvyklım zpùsobem. Pouijte tuto volbu, pokud chcete nainstalovat tuto novìjší verzi do jiné sloky ne pøedchozí verzi."
!endif


!ifdef UMUI_MAINTENANCEPAGE | UMUI_UNMAINTENANCEPAGE
  ${LangFileString} UMUI_TEXT_MAINTENANCE_TITLE "Údrba"
  ${LangFileString} UMUI_TEXT_MAINTENANCE_SUBTITLE "Zmìna, oprava nebo odstranìní programu."
  ${LangFileString} UMUI_TEXT_MAINTENANCE_INFO_TEXT "Vítejte v prùvodci údrbou programu$(^NameDA).$\r$\nPomocí tohoto prùvodce mùete modifikovat aktuální instalaci."
  ${LangFileString} UMUI_TEXT_MAINTENANCE_MODIFY_TITLE "Zmìnit"
  ${LangFileString} UMUI_TEXT_MAINTENANCE_MODIFY_TEXT "Vyberte nové souèásti, které budou pøidány nebo vyberte nainstalované souèásti, které budou odebrány."
  ${LangFileString} UMUI_TEXT_MAINTENANCE_REPAIR_TITLE "Opravit"
  ${LangFileString} UMUI_TEXT_MAINTENANCE_REPAIR_TEXT "Pøeinstalovat všechny souèásti $(^NameDA)."
  ${LangFileString} UMUI_TEXT_MAINTENANCE_REMOVE_TITLE "Odebrat"
  ${LangFileString} UMUI_TEXT_MAINTENANCE_REMOVE_TEXT "Odebrat $(^NameDA) z vašeho poèítaèe."
  ${LangFileString} UMUI_TEXT_MAINTENANCE_CONTINUE_TITLE "Pokraèovat"
  ${LangFileString} UMUI_TEXT_MAINTENANCE_CONTINUE_TEXT "Pokraèovat v prùvodci obvyklım zpùsobem. Pouijte tuto monost, pokud chcete pøeinstalovat stávající instalaci programu nebo nainstalovat jej znovu do jiné sloky."
!endif


!ifdef UMUI_FILEDISKREQUESTPAGE | UMUI_UNFILEDISKREQUESTPAGE
  ${LangFileString} UMUI_TEXT_FILEDISKREQUEST_FILE_SUBTITLE_BEGIN "Prùvodce vyaduje soubor"
  ${LangFileString} UMUI_TEXT_FILEDISKREQUEST_FILE_SUBTITLE_END "aby mohl pokraèovat."
  ${LangFileString} UMUI_TEXT_FILEDISKREQUEST_DISK_SUBTITLE "Vlote další disk pro pokraèování."
  ${LangFileString} UMUI_TEXT_FILEDISKREQUEST_FILE_BEGIN "Urèete umístìní souboru"
  ${LangFileString} UMUI_TEXT_FILEDISKREQUEST_FILE_END "aby mohla instalace pokraèovat."
  ${LangFileString} UMUI_TEXT_FILEDISKREQUEST_DISK "Prosím vlote"
  ${LangFileString} UMUI_TEXT_FILEDISKREQUEST_PATH "Cesta:"
!endif
