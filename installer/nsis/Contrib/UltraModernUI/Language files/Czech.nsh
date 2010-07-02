;NSIS Modern User Interface - Language File
;Compatible with UltraModernUI 1.00 beta 2

;Language: Czech (1029)
;By Pospec (pospec4444atgmaildotcom)

;--------------------------------

!ifdef UMUI_MULTILANGUAGEPAGE
  ${LangFileString} UMUI_TEXT_MULTILANGUAGE_TITLE "V�tejte v pr�vodci instalac� $(^NameDA)"
  ${LangFileString} UMUI_TEXT_MULTILANGUAGE_TEXT "P�ed za��tkem instalace $(^NameDA) vyberte, pros�m, jazyk:$\r$\n$\r$\n$_CLICK"
!endif

!ifdef UMUI_UNMULTILANGUAGEPAGE
  ${LangFileString} UMUI_UNTEXT_MULTILANGUAGE_TITLE "V�tejte v pr�vodci odebr�n�m $(^NameDA)"
  ${LangFileString} UMUI_UNTEXT_MULTILANGUAGE_TEXT "P�ed za��tkem odinstalace $(^NameDA) vyberte, pros�m, jazyk:$\r$\n$\r$\n$_CLICK"
!endif

!ifdef UMUI_MULTILANGUAGEPAGE | UMUI_UNMULTILANGUAGEPAGE
  ${LangFileString} UMUI_TEXT_MULTILANGUAGE_LANGUAGE "Jazyk:"
!endif


!ifdef MUI_WELCOMEPAGE
  ${LangFileString} UMUI_TEXT_WELCOME_ALTERNATIVEINFO_TEXT "Tento pr�vodce v�s provede instalac� $(^NameDA).$\r$\n$\r$\n$\r$\n$_CLICK"
!endif


!ifdef UMUI_SERIALNUMBERPAGE | UMUI_UNSERIALNUMBERPAGE
  ${LangFileString} UMUI_TEXT_SERIALNUMBER_TITLE "Zadejte s�riov� ��slo pro $(^NameDA)"
  ${LangFileString} UMUI_TEXT_SERIALNUMBER_SUBTITLE "Vypl�te pole n�e, pros�m."
  ${LangFileString} UMUI_TEXT_SERIALNUMBER_INFO_TEXT "Vypl�te pole n�e, pros�m. $_CLICK"
  ${LangFileString} UMUI_TEXT_SERIALNUMBER_INVALIDATE_TEXT "$UMUI_SNTEXT je nespr�vn�. Zkontrolujte, pros�m, zda jste zadali spr�vn� �daje."
  ${LangFileString} UMUI_TEXT_SERIALNUMBER_NAME "Jm�no"
  ${LangFileString} UMUI_TEXT_SERIALNUMBER_ORGANIZATION "Organizace"
  ${LangFileString} UMUI_TEXT_SERIALNUMBER_SERIALNUMBER "S�riov� ��slo"
  ${LangFileString} UMUI_TEXT_SERIALNUMBER_ACTIVATIONCODE "Aktiva�n� k�d"
  ${LangFileString} UMUI_TEXT_SERIALNUMBER_PASSWORD "Heslo"
!endif 


!ifdef UMUI_CONFIRMPAGE
  ${LangFileString} UMUI_TEXT_INSTCONFIRM_SUBTITLE "Pr�vodce z�skal v�echny pot�ebn� informace a je p�ipraven nainstalovat produkt $(^NameDA)."
  ${LangFileString} UMUI_TEXT_INSTCONFIRM_TITLE "Potvrdit instalaci"
  ${LangFileString} UMUI_TEXT_INSTCONFIRM_TEXT_TOP "Pr�vodce je p�ipraven nainstalovat $(^NameDA) na v� po��ta�.$\r$\nPokud chcete zkontrolovat nebo zm�nit n�kter� parametry instalace, klikn�te na Zp�t. $_CLICK"
!endif 

!ifdef UMUI_UNCONFIRMPAGE
  ${LangFileString} UMUI_UNTEXT_INSTCONFIRM_SUBTITLE "Pr�vodce z�skal v�echny pot�ebn� informace a je p�ipraven odebrat produkt $(^NameDA)."
  ${LangFileString} UMUI_UNTEXT_INSTCONFIRM_TITLE "Potvrdit odebr�n�"
  ${LangFileString} UMUI_UNTEXT_INSTCONFIRM_TEXT_TOP "Pr�vodce je p�ipraven odebrat $(^NameDA) na v� po��ta�.$\r$\nPokud chcete zkontrolovat nebo zm�nit n�kter� parametry odebrat, klikn�te na Zp�t. Klikn�te na Dal�� pro odebr�n�."
!endif 

!ifdef UMUI_CONFIRMPAGE | UMUI_UNCONFIRMPAGE
  ${LangFileString} UMUI_TEXT_INSTCONFIRM_TEXTBOX_TITLE "St�vaj�c� nastaven�:"
  ${LangFileString} UMUI_TEXT_INSTCONFIRM_TEXTBOX_DESTINATION_LOCATION "C�lov� slo�ka:"
  ${LangFileString} UMUI_TEXT_INSTCONFIRM_TEXTBOX_START_MENU_FOLDER "Nab�dka Start:"
  ${LangFileString} UMUI_TEXT_INSTCONFIRM_TEXTBOX_COMPNENTS "Budou instalov�ny n�sleduj�c� komponenty:"
!endif 


!ifdef UMUI_ABORTPAGE
  ${LangFileString} UMUI_TEXT_ABORT_INFO_TITLE "Dokon�ov�n� pr�vodce instalac� $(^NameDA)"
  ${LangFileString} UMUI_TEXT_ABORT_INFO_TEXT "Pr�vodce instalac� byl ukon�en d��ve, ne� mohl b�t $(^NameDA) kompletn� nainstalovan�.$\r$\n$\r$\nPro pozd�j�� instalace spus�te, pros�m, instal�tor znovu.$\r$\n$\r$\n$\r$\n$\r$\nKlikn�te na $(^CloseBtn) pro ukon�en� pr�vodce instalac�."
!endif 

!ifdef UMUI_UNABORTPAGE
  ${LangFileString} UMUI_UNTEXT_ABORT_INFO_TITLE "Dokon�ov�n� pr�vodce odebr�n�m $(^NameDA)"
  ${LangFileString} UMUI_UNTEXT_ABORT_INFO_TEXT "Pr�vodce odebr�n�m byl ukon�en d��ve, ne� mohl b�t $(^NameDA) kompletn� odebr�n.$\r$\n$\r$\nPro pozd�j�� odebr�n� spus�te, pros�m, tohoto pr�vodce znovu.$\r$\n$\r$\n$\r$\n$\r$\nKlikn�te na $(^CloseBtn) pro ukon�en� pr�vodce odebr�n�m."
!endif


!ifdef UMUI_SETUPTYPEPAGE
  ${LangFileString} UMUI_TEXT_SETUPTYPE_TITLE "Typ instalace"
  ${LangFileString} UMUI_TEXT_SETUPTYPE_SUBTITLE "Pros�m, vyberte typ instalace, kter� v�m vyhovuje."
  ${LangFileString} UMUI_TEXT_SETUPTYPE_INFO_TEXT "Pros�m, vyberte typ instalace."
  ${LangFileString} UMUI_TEXT_SETUPTYPE_MINIMAL_TEXT "Budou instalov�ny jen nutn� sou��sti. (�et�� m�sto na disku)"
  ${LangFileString} UMUI_TEXT_SETUPTYPE_STANDARD_TEXT "Budou se instalovat v�echny d�le�it� sou��sti. Doporu�uje se pro v�t�inu u�ivatel�."
  ${LangFileString} UMUI_TEXT_SETUPTYPE_COMPLETE_TEXT "Budou se instalovat v�echny sou��sti. (Pot�ebuje nejv�ce m�sta na disku)"
  ${LangFileString} UMUI_TEXT_SETUPTYPE_CUSTOM_TEXT "Vyberte sou��sti, kter� se budou instalovat a ur�ete, kam se nainstaluj�. Doporu�uje se pro zku�en� u�ivatele."
!endif 

!ifdef UMUI_UNSETUPTYPEPAGE
  ${LangFileString} UMUI_UNTEXT_SETUPTYPE_TITLE "Typ odinstalace"
  ${LangFileString} UMUI_UNTEXT_SETUPTYPE_SUBTITLE "Pros�m, vyberte typ odinstalace, kter� v�m vyhovuje."
  ${LangFileString} UMUI_UNTEXT_SETUPTYPE_INFO_TEXT "Pros�m, vyberte typ odinstalace."
  ${LangFileString} UMUI_UNTEXT_SETUPTYPE_MINIMAL_TEXT "Bude ponech�na v�t�ina sou��st�."
  ${LangFileString} UMUI_UNTEXT_SETUPTYPE_STANDARD_TEXT "Budou ponech�ny nutn� sou��st�."
  ${LangFileString} UMUI_UNTEXT_SETUPTYPE_COMPLETE_TEXT "Cel� aplikace bude odstran�na."
  ${LangFileString} UMUI_UNTEXT_SETUPTYPE_CUSTOM_TEXT "Vyberte sou��sti programu, kter� se odeberou."
!endif 

!ifdef UMUI_SETUPTYPEPAGE | UMUI_UNSETUPTYPEPAGE
  ${LangFileString} UMUI_TEXT_SETUPTYPE_MINIMAL_TITLE "Minim�ln�"
  ${LangFileString} UMUI_TEXT_SETUPTYPE_STANDARD_TITLE "Standardn�"
  ${LangFileString} UMUI_TEXT_SETUPTYPE_COMPLETE_TITLE "�pln�"
  ${LangFileString} UMUI_TEXT_SETUPTYPE_CUSTOM_TITLE "Vlastn�"
!endif 


!ifdef UMUI_INFORMATIONPAGE
  ${LangFileString} UMUI_TEXT_INFORMATION_SUBTITLE "Pros�m, vezm�te v �vahu tyto informace t�kaj�c� se instalace $(^NameDA)."
!endif 

!ifdef UMUI_UNINFORMATIONPAGE
  ${LangFileString} UMUI_UNTEXT_INFORMATION_SUBTITLE "Pros�m, vezm�te v �vahu tyto informace t�kaj�c� se odebr�n� $(^NameDA)."
!endif 

!ifdef UMUI_INFORMATIONPAGE | UMUI_UNINFORMATIONPAGE
  ${LangFileString} UMUI_TEXT_INFORMATION_TITLE "Informace"
  ${LangFileString} UMUI_TEXT_INFORMATION_INFO_TEXT "Informace t�kaj�c� se $(^NameDA)."
!endif 


!ifdef UMUI_ADDITIONALTASKSPAGE
  ${LangFileString} UMUI_TEXT_ADDITIONALTASKS_INFO_TEXT "Vyberte dodate�n� akce, kter� m� pr�vodce prov�st v pr�b�hu instalace $(^NameDA). $_CLICK"
!endif 

!ifdef UMUI_UNADDITIONALTASKSPAGE
  ${LangFileString} UMUI_UNTEXT_ADDITIONALTASKS_INFO_TEXT "Vyberte dodate�n� akce, kter� m� pr�vodce prov�st v pr�b�hu odebr�n� $(^NameDA). $_CLICK"
!endif 

!ifdef UMUI_ADDITIONALTASKSPAGE | UMUI_UNADDITIONALTASKSPAGE
  ${LangFileString} UMUI_TEXT_ADDITIONALTASKS_TITLE "Dodate�n� akce"
  ${LangFileString} UMUI_TEXT_ADDITIONALTASKS_SUBTITLE "Kter� dodate�n� akce chcete prov�st?"
  ${LangFileString} UMUI_TEXT_ADDITIONALTASKS_ADDITIONAL_ICONS "Ikony:"
  ${LangFileString} UMUI_TEXT_ADDITIONALTASKS_CREATE_DESKTOP_ICON "Vytvo�it ikonu na plo�e"
  ${LangFileString} UMUI_TEXT_ADDITIONALTASKS_CREATE_QUICK_LAUNCH_ICON "Vytvo�it ikonu snadn�ho spu�t�n�"
  ${LangFileString} UMUI_TEXT_ADDITIONALTASKS_ADVANCED_PARAMETERS "Dalsi parametry:"
  ${LangFileString} UMUI_TEXT_ADDITIONALTASKS_LAUNCH_PROGRAM_AT_WINDOWS_STARTUP "Spou�t�t $(^NameDA) po startu Windows"
  ${LangFileString} UMUI_TEXT_ADDITIONALTASKS_FILE_ASSOCIATION "Asociace soubor�:"
  ${LangFileString} UMUI_TEXT_ADDITIONALTASKS_ASSOCIATE_WITH "Asociovat $(^NameDA) s "
  ${LangFileString} UMUI_TEXT_ADDITIONALTASKS_ASSOCIATE_WITH_END " soubory"
!endif 
  
  
!ifdef UMUI_CONFIRMPAGE | UMUI_UNCONFIRMPAGE | UMUI_ALTERNATIVESTARTMENUPAGE | UMUI_UNALTERNATIVESTARTMENUPAGE
  ${LangFileString} UMUI_TEXT_SHELL_VAR_CONTEXT "Vytvo�it z�stupce pro:"
  ${LangFileString} UMUI_TEXT_SHELL_VAR_CONTEXT_FOR_ALL_USERS "V�echny u�ivatele"
  ${LangFileString} UMUI_TEXT_SHELL_VAR_CONTEXT_ONLY_FOR_CURRENT_USER "Jen pro m�"
!endif


!ifdef UMUI_UPDATEPAGE
  ${LangFileString} UMUI_TEXT_UPDATE_TITLE "Aktualizovat"
  ${LangFileString} UMUI_TEXT_UPDATE_SUBTITLE "Aktualizace p�edchoz� verze programu."
  ${LangFileString} UMUI_TEXT_UPDATE_INFO_TEXT "V�tejte v pr�vodci aktualizac� $(^NameDA) .$\r$\nTento pr�vodce umo��uje aktualizovat $OLDVERSION nalezenou na va�em po��ta�i."
  ${LangFileString} UMUI_TEXT_UPDATE_UPDATE_TITLE "Aktualizovat"
  ${LangFileString} UMUI_TEXT_UPDATE_UPDATE_TEXT "Aktualizovat v�echny sou��sti $(^NameDA) nainstalovan� p�ed verz� $NEWVERSION.."
  ${LangFileString} UMUI_TEXT_UPDATE_REMOVE_TITLE "Odebrat"
  ${LangFileString} UMUI_TEXT_UPDATE_REMOVE_TEXT "Odebrat $(^NameDA) z va�eho po��ta�e."
  ${LangFileString} UMUI_TEXT_UPDATE_CONTINUE_TITLE "Pokra�ovat v instalaci"
  ${LangFileString} UMUI_TEXT_UPDATE_CONTINUE_TEXT "Pokra�ovat v instalaci obvykl�m zp�sobem. Pou�ijte tuto volbu, pokud chcete nainstalovat tuto nov�j�� verzi do jin� slo�ky ne� p�edchoz� verzi."
!endif


!ifdef UMUI_MAINTENANCEPAGE | UMUI_UNMAINTENANCEPAGE
  ${LangFileString} UMUI_TEXT_MAINTENANCE_TITLE "�dr�ba"
  ${LangFileString} UMUI_TEXT_MAINTENANCE_SUBTITLE "Zm�na, oprava nebo odstran�n� programu."
  ${LangFileString} UMUI_TEXT_MAINTENANCE_INFO_TEXT "V�tejte v pr�vodci �dr�bou programu$(^NameDA).$\r$\nPomoc� tohoto pr�vodce m��ete modifikovat aktu�ln� instalaci."
  ${LangFileString} UMUI_TEXT_MAINTENANCE_MODIFY_TITLE "Zm�nit"
  ${LangFileString} UMUI_TEXT_MAINTENANCE_MODIFY_TEXT "Vyberte nov� sou��sti, kter� budou p�id�ny nebo vyberte nainstalovan� sou��sti, kter� budou odebr�ny."
  ${LangFileString} UMUI_TEXT_MAINTENANCE_REPAIR_TITLE "Opravit"
  ${LangFileString} UMUI_TEXT_MAINTENANCE_REPAIR_TEXT "P�einstalovat v�echny sou��sti $(^NameDA)."
  ${LangFileString} UMUI_TEXT_MAINTENANCE_REMOVE_TITLE "Odebrat"
  ${LangFileString} UMUI_TEXT_MAINTENANCE_REMOVE_TEXT "Odebrat $(^NameDA) z va�eho po��ta�e."
  ${LangFileString} UMUI_TEXT_MAINTENANCE_CONTINUE_TITLE "Pokra�ovat"
  ${LangFileString} UMUI_TEXT_MAINTENANCE_CONTINUE_TEXT "Pokra�ovat v pr�vodci obvykl�m zp�sobem. Pou�ijte tuto mo�nost, pokud chcete p�einstalovat st�vaj�c� instalaci programu nebo nainstalovat jej znovu do jin� slo�ky."
!endif


!ifdef UMUI_FILEDISKREQUESTPAGE | UMUI_UNFILEDISKREQUESTPAGE
  ${LangFileString} UMUI_TEXT_FILEDISKREQUEST_FILE_SUBTITLE_BEGIN "Pr�vodce vy�aduje soubor"
  ${LangFileString} UMUI_TEXT_FILEDISKREQUEST_FILE_SUBTITLE_END "aby mohl pokra�ovat."
  ${LangFileString} UMUI_TEXT_FILEDISKREQUEST_DISK_SUBTITLE "Vlo�te dal�� disk pro pokra�ov�n�."
  ${LangFileString} UMUI_TEXT_FILEDISKREQUEST_FILE_BEGIN "Ur�ete um�st�n� souboru"
  ${LangFileString} UMUI_TEXT_FILEDISKREQUEST_FILE_END "aby mohla instalace pokra�ovat."
  ${LangFileString} UMUI_TEXT_FILEDISKREQUEST_DISK "Pros�m vlo�te"
  ${LangFileString} UMUI_TEXT_FILEDISKREQUEST_PATH "Cesta:"
!endif
