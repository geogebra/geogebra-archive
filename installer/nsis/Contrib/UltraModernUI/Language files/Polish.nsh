;NSIS Modern User Interface - Language File
;Compatible with UltraModernUI 1.00 beta 2

;Language: Polish (1045)
;By forge

;--------------------------------

!ifdef UMUI_MULTILANGUAGEPAGE
  ${LangFileString} UMUI_TEXT_MULTILANGUAGE_TITLE "Witamy w instalatorze $(^NameDA)"
  ${LangFileString} UMUI_TEXT_MULTILANGUAGE_TEXT "Przed rozpocz�ciem instalacji $(^NameDA), prosz� wybra� j�zyk:$\r$\n$\r$\n$_CLICK"
!endif

!ifdef UMUI_UNMULTILANGUAGEPAGE
  ${LangFileString} UMUI_UNTEXT_MULTILANGUAGE_TITLE "Witamy w deinstalatorze $(^NameDA)"
  ${LangFileString} UMUI_UNTEXT_MULTILANGUAGE_TEXT "Przed rozpocz�ciem deinstalacji $(^NameDA), prosz� wybra� j�zyk:$\r$\n$\r$\n$_CLICK"
!endif

!ifdef UMUI_MULTILANGUAGEPAGE | UMUI_UNMULTILANGUAGEPAGE
  ${LangFileString} UMUI_TEXT_MULTILANGUAGE_LANGUAGE "J�zyk:"
!endif


!ifdef MUI_WELCOMEPAGE
  ${LangFileString} UMUI_TEXT_WELCOME_ALTERNATIVEINFO_TEXT "Ten przewodnik przeprowadzi Ci� przez proces instalacji $(^NameDA).$\r$\n$\r$\n$\r$\n$_CLICK"
!endif


!ifdef UMUI_SERIALNUMBERPAGE | UMUI_UNSERIALNUMBERPAGE
  ${LangFileString} UMUI_TEXT_SERIALNUMBER_TITLE "Wprowad� numer seryjny dla $(^NameDA)"
  ${LangFileString} UMUI_TEXT_SERIALNUMBER_SUBTITLE "Prosz� wype�nij pola poni�ej."
  ${LangFileString} UMUI_TEXT_SERIALNUMBER_INFO_TEXT "Prosz� wype�nij pola poni�ej. $_CLICK"
  ${LangFileString} UMUI_TEXT_SERIALNUMBER_INVALIDATE_TEXT "$UMUI_SNTEXT jest nieprawid�owy. Prosz� sprawd� wprowadzone informacje."
  ${LangFileString} UMUI_TEXT_SERIALNUMBER_NAME "Nazwa"
  ${LangFileString} UMUI_TEXT_SERIALNUMBER_ORGANIZATION "Organizacja"
  ${LangFileString} UMUI_TEXT_SERIALNUMBER_SERIALNUMBER "Numer Seryjny"
  ${LangFileString} UMUI_TEXT_SERIALNUMBER_ACTIVATIONCODE "Kod Aktywacyjny"
  ${LangFileString} UMUI_TEXT_SERIALNUMBER_PASSWORD "Has�o"
!endif 


!ifdef UMUI_CONFIRMPAGE
  ${LangFileString} UMUI_TEXT_INSTCONFIRM_SUBTITLE "Instalator zako�czy� zbieranie informacji i jest gotowy do instalacji $(^NameDA)."
  ${LangFileString} UMUI_TEXT_INSTCONFIRM_TITLE "Potwierd� ch�� instalacji"
  ${LangFileString} UMUI_TEXT_INSTCONFIRM_TEXT_TOP "Instalator jest gotowy do instalacji $(^NameDA) na Twoim komputerze.$\r$\nJe�li chcesz sprawdzi� lub zmieni� ustawienia instalatora, kliknij Wstecz. $_CLICK"
!endif 

!ifdef UMUI_UNCONFIRMPAGE
  ${LangFileString} UMUI_UNTEXT_INSTCONFIRM_SUBTITLE "Instalator zako�czy� zbieranie informacji i jest gotowy do deinstalacji $(^NameDA)."
  ${LangFileString} UMUI_UNTEXT_INSTCONFIRM_TITLE "Potwierd� ch�� Deinstalacji"
  ${LangFileString} UMUI_UNTEXT_INSTCONFIRM_TEXT_TOP "Instalator jest gotowy do deinstalacji $(^NameDA) z Twojego komputera.$\r$\nJe�li chcesz sprawdzi� lub zmieni� ustawienia deinstalatora, kliknij Wstecz. Kliknij Dalej aby rozpocz�� deinstalacj�."
!endif 

!ifdef UMUI_CONFIRMPAGE | UMUI_UNCONFIRMPAGE
  ${LangFileString} UMUI_TEXT_INSTCONFIRM_TEXTBOX_TITLE "Obecna konfiguracja:"
  ${LangFileString} UMUI_TEXT_INSTCONFIRM_TEXTBOX_DESTINATION_LOCATION "Lokalizacja docelowa:"
  ${LangFileString} UMUI_TEXT_INSTCONFIRM_TEXTBOX_START_MENU_FOLDER "Katalog w Menu Start:"
  ${LangFileString} UMUI_TEXT_INSTCONFIRM_TEXTBOX_COMPNENTS "Nat�puj�ce sk��dniki zostan� zainstalowane:"
!endif 


!ifdef UMUI_ABORTPAGE
  ${LangFileString} UMUI_TEXT_ABORT_INFO_TITLE "Ko�czenie instalacji $(^NameDA)"
!endif 
  ${LangFileString} UMUI_TEXT_ABORT_INFO_TEXT "Praca instalatora $(^NameDA) zosta�a przerwana przed zako�czeniem instalcji.$\r$\n$\r$\nAby p�nije zainstalowa� program, prosz� uruchomi� instalator ponownie.$\r$\n$\r$\n$\r$\n$\r$\nKliknij $(^CloseBtn) aby opu�ci� instalator."

!ifdef UMUI_UNABORTPAGE
  ${LangFileString} UMUI_UNTEXT_ABORT_INFO_TITLE "Ko�czenie deinstalacji $(^NameDA)"
  ${LangFileString} UMUI_UNTEXT_ABORT_INFO_TEXT "Praca deinstalatora $(^NameDA) zosta�a przerwana przed zako�czeniem deinstalcji.$\r$\n$\r$\nAby p�niej odinstalowa� program, prosz� uruchomi� deinstaltor ponownie.$\r$\n$\r$\n$\r$\n$\r$\nKliknij $(^CloseBtn) aby opu�ci� deinstalator."
!endif


!ifdef UMUI_SETUPTYPEPAGE
  ${LangFileString} UMUI_TEXT_SETUPTYPE_TITLE "Typ instalacji"
  ${LangFileString} UMUI_TEXT_SETUPTYPE_SUBTITLE "Wybierz typ instalacji, kt�ry najbardziej odpowiada Twoim potrzebom."
  ${LangFileString} UMUI_TEXT_SETUPTYPE_INFO_TEXT "Prosz� wybra� typ instalacji."
  ${LangFileString} UMUI_TEXT_SETUPTYPE_MINIMAL_TEXT "Tylko niezb�dne sk�adniki programu zostan� zainstalowane. (Wymaga najmniej miejsca na dysku)"
  ${LangFileString} UMUI_TEXT_SETUPTYPE_STANDARD_TEXT "Najwa�niejsze sk�adniki programu zostan� zainstalowane. Sugerowana dla wiekszo�ci u�ytkownik�w."
  ${LangFileString} UMUI_TEXT_SETUPTYPE_COMPLETE_TEXT "Wszystkie sk�adniki programu zostan� zainstalowane. (Wymaga najwiecej miejsca na dysku)"
  ${LangFileString} UMUI_TEXT_SETUPTYPE_CUSTOM_TEXT "Wybierz kt�re sk��dniki programu chcesz zainstalowa� i gdzie maj� by� zainstalowane. Zalecane dla u�ytkownik�w zaawansowanych."
!endif 

!ifdef UMUI_UNSETUPTYPEPAGE
  ${LangFileString} UMUI_UNTEXT_SETUPTYPE_TITLE "Typ deinstalacji"
  ${LangFileString} UMUI_UNTEXT_SETUPTYPE_SUBTITLE "Wybierz typ deinstalacji, kt�ry najbardziej odpowiada Twoim potrzebom."
  ${LangFileString} UMUI_UNTEXT_SETUPTYPE_INFO_TEXT "Prosz� wybra� typ deinstalacji."
  ${LangFileString} UMUI_UNTEXT_SETUPTYPE_MINIMAL_TEXT "Najwa�niejsze sk�adniki programu zostan� zachowane."
  ${LangFileString} UMUI_UNTEXT_SETUPTYPE_STANDARD_TEXT "Tylko niezb�dne sk�adniki programu zostan� zachowane."
  ${LangFileString} UMUI_UNTEXT_SETUPTYPE_COMPLETE_TEXT "Wszystkie sk�adniki programu zostan� odinstalowane."
  ${LangFileString} UMUI_UNTEXT_SETUPTYPE_CUSTOM_TEXT "Wybierz kt�re sk�adniki programu chcesz odinstalowa�."
!endif 

!ifdef UMUI_SETUPTYPEPAGE | UMUI_UNSETUPTYPEPAGE
  ${LangFileString} UMUI_TEXT_SETUPTYPE_MINIMAL_TITLE "Minimalna"
  ${LangFileString} UMUI_TEXT_SETUPTYPE_STANDARD_TITLE "Standardowa"
  ${LangFileString} UMUI_TEXT_SETUPTYPE_COMPLETE_TITLE "Kompletna"
  ${LangFileString} UMUI_TEXT_SETUPTYPE_CUSTOM_TITLE "U�ytkownika"
!endif 


!ifdef UMUI_INFORMATIONPAGE
  ${LangFileString} UMUI_TEXT_INFORMATION_SUBTITLE "Sprawd� informacje dotycz�ce instalacji programu $(^NameDA)."
!endif 

!ifdef UMUI_UNINFORMATIONPAGE
  ${LangFileString} UMUI_UNTEXT_INFORMATION_SUBTITLE "Sprawd� informacje dotycz�ce deinstalacji programu $(^NameDA)."
!endif 

!ifdef UMUI_INFORMATIONPAGE | UMUI_UNINFORMATIONPAGE
  ${LangFileString} UMUI_TEXT_INFORMATION_TITLE "Informacje"
  ${LangFileString} UMUI_TEXT_INFORMATION_INFO_TEXT "Informacje dotycz�ce $(^NameDA)."
!endif 


!ifdef UMUI_ADDITIONALTASKSPAGE
  ${LangFileString} UMUI_TEXT_ADDITIONALTASKS_INFO_TEXT "Wybierz dodatkowe zadania, kt�re powinny by� wykonane podczas procesu instalcji $(^NameDA). $_CLICK"
!endif 

!ifdef UMUI_UNADDITIONALTASKSPAGE
  ${LangFileString} UMUI_UNTEXT_ADDITIONALTASKS_INFO_TEXT "Wybierz dodatkowe zadania, kt�re powinny by� wykonane podczas procesu deinstalcji $(^NameDA). $_CLICK"
!endif 

!ifdef UMUI_ADDITIONALTASKSPAGE | UMUI_UNADDITIONALTASKSPAGE
  ${LangFileString} UMUI_TEXT_ADDITIONALTASKS_TITLE "Dodatkowe Zadania"
  ${LangFileString} UMUI_TEXT_ADDITIONALTASKS_SUBTITLE "Kt�re z dodatkowych czynno�ci chcesz wykona�?"
  ${LangFileString} UMUI_TEXT_ADDITIONALTASKS_ADDITIONAL_ICONS "Dodatkowe ikony:"
  ${LangFileString} UMUI_TEXT_ADDITIONALTASKS_CREATE_DESKTOP_ICON "Utw�rz ikone na pulpicie"
  ${LangFileString} UMUI_TEXT_ADDITIONALTASKS_CREATE_QUICK_LAUNCH_ICON "Utw�rz ikone w pasku szybkiego uruchamiania"
  ${LangFileString} UMUI_TEXT_ADDITIONALTASKS_ADVANCED_PARAMETERS "Zaawansowane ustawienia:"
  ${LangFileString} UMUI_TEXT_ADDITIONALTASKS_LAUNCH_PROGRAM_AT_WINDOWS_STARTUP "Uruchom $(^NameDA) przy starcie systemu windows"
  ${LangFileString} UMUI_TEXT_ADDITIONALTASKS_FILE_ASSOCIATION "Skojarzenia plik�w:"
  ${LangFileString} UMUI_TEXT_ADDITIONALTASKS_ASSOCIATE_WITH "Skojarz $(^NameDA) z "
  ${LangFileString} UMUI_TEXT_ADDITIONALTASKS_ASSOCIATE_WITH_END " typem pliku"
!endif 
  
  
!ifdef UMUI_CONFIRMPAGE | UMUI_UNCONFIRMPAGE | UMUI_ALTERNATIVESTARTMENUPAGE | UMUI_UNALTERNATIVESTARTMENUPAGE
  ${LangFileString} UMUI_TEXT_SHELL_VAR_CONTEXT "Jak utworzy� skr�ty:"
  ${LangFileString} UMUI_TEXT_SHELL_VAR_CONTEXT_FOR_ALL_USERS "Dla wszystkich u�ytkownik�w"
  ${LangFileString} UMUI_TEXT_SHELL_VAR_CONTEXT_ONLY_FOR_CURRENT_USER "Tylko dla obecnego u�ytkownika"
!endif


!ifdef UMUI_UPDATEPAGE
  ${LangFileString} UMUI_TEXT_UPDATE_TITLE "Aktualizacja"
  ${LangFileString} UMUI_TEXT_UPDATE_SUBTITLE "Aktualizuj poprzeni� wersj� programu."
  ${LangFileString} UMUI_TEXT_UPDATE_INFO_TEXT "Witaj w przewodniku aktualizacji $(^NameDA).$\r$\nTen program pozwoli Ci zaktualizaowa� wersj� $OLDVERSION znalezion� na Twoim komputerze."
  ${LangFileString} UMUI_TEXT_UPDATE_UPDATE_TITLE "Aktualizuj"
  ${LangFileString} UMUI_TEXT_UPDATE_UPDATE_TEXT "Aktualizuj wszystkie zainstalowne sk�adniki $(^NameDA) do wersji $NEWVERSION.."
  ${LangFileString} UMUI_TEXT_UPDATE_REMOVE_TITLE "Usu�"
  ${LangFileString} UMUI_TEXT_UPDATE_REMOVE_TEXT "Odinstaluj $(^NameDA) z Twojego komputera."
  ${LangFileString} UMUI_TEXT_UPDATE_CONTINUE_TITLE "Kontynuowanie instalacji"
  ${LangFileString} UMUI_TEXT_UPDATE_CONTINUE_TEXT "Kontynuuj normaln� instalacje. U�yj tej opcji aby zainstalowa� nowasz� wersj� w innym katalogu ni� dotychczasowa instalacja."
!endif


!ifdef UMUI_MAINTENANCEPAGE | UMUI_UNMAINTENANCEPAGE
  ${LangFileString} UMUI_TEXT_MAINTENANCE_TITLE "Konserwacja"
  ${LangFileString} UMUI_TEXT_MAINTENANCE_SUBTITLE "Modyfikuj, napraw, lub usu� program."
  ${LangFileString} UMUI_TEXT_MAINTENANCE_INFO_TEXT "Witamy w instalatorze programu $(^NameDA) .$\r$\nTen program umo�liwi Ci modyfikacj� obecnej instalacji."
  ${LangFileString} UMUI_TEXT_MAINTENANCE_MODIFY_TITLE "Modyfikuj"
  ${LangFileString} UMUI_TEXT_MAINTENANCE_MODIFY_TEXT "Zaznacz nowe sk�adniki do dodanie lub zaznacz ju� zainstalowane aby je usun��."
  ${LangFileString} UMUI_TEXT_MAINTENANCE_REPAIR_TITLE "Napraw"
  ${LangFileString} UMUI_TEXT_MAINTENANCE_REPAIR_TEXT "Reinstaluje wszystkie sk�adniki $(^NameDA), kt�re ju� zosta�y zainstalwane."
  ${LangFileString} UMUI_TEXT_MAINTENANCE_REMOVE_TITLE "Usu�"
  ${LangFileString} UMUI_TEXT_MAINTENANCE_REMOVE_TEXT "Odinstaluj $(^NameDA) z Twojego komputera."
  ${LangFileString} UMUI_TEXT_MAINTENANCE_CONTINUE_TITLE "Kontynuowanie instalacji"
  ${LangFileString} UMUI_TEXT_MAINTENANCE_CONTINUE_TEXT "Kontynuuj normaln� instalacje. U�yj tej opcji aby przeinstalowa� Use this option if you want to reinstall this program over an existing install or to install it a new time in a different folder."
!endif


!ifdef UMUI_FILEDISKREQUESTPAGE | UMUI_UNFILEDISKREQUESTPAGE
  ${LangFileString} UMUI_TEXT_FILEDISKREQUEST_FILE_SUBTITLE_BEGIN "Instalator potrzebuje"
  ${LangFileString} UMUI_TEXT_FILEDISKREQUEST_FILE_SUBTITLE_END "pliku aby kontynuowa�."
  ${LangFileString} UMUI_TEXT_FILEDISKREQUEST_DISK_SUBTITLE "Instaltor potrzebuje kolejnego dysku."
  ${LangFileString} UMUI_TEXT_FILEDISKREQUEST_FILE_BEGIN "Wprowad� lokalizacje pliku"
  ${LangFileString} UMUI_TEXT_FILEDISKREQUEST_FILE_END "aby kontynuowa�."
  ${LangFileString} UMUI_TEXT_FILEDISKREQUEST_DISK "Prosz� w��"
  ${LangFileString} UMUI_TEXT_FILEDISKREQUEST_PATH "�cie�ka:"
!endif
