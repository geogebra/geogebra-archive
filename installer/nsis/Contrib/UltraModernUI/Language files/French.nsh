;NSIS Modern User Interface - Language File
;Compatible with UltraModernUI 1.00 beta 2

;Language: French (1036)
;By SuperPat

;--------------------------------

!ifdef UMUI_MULTILANGUAGEPAGE
  ${LangFileString} UMUI_TEXT_MULTILANGUAGE_TITLE "Bienvenue dans le programme d'installation de $(^NameDA)"
  ${LangFileString} UMUI_TEXT_MULTILANGUAGE_TEXT "Avant de commencer l'installation de $(^NameDA), veuillez choisir un langage:$\r$\n$\r$\n$_CLICK"
!endif

!ifdef UMUI_UNMULTILANGUAGEPAGE
  ${LangFileString} UMUI_UNTEXT_MULTILANGUAGE_TITLE "Bienvenue dans le programme de d�sinstallation de $(^NameDA)"
  ${LangFileString} UMUI_UNTEXT_MULTILANGUAGE_TEXT "Avant de commencer la d�sinstallation de $(^NameDA), veuillez choisir un langage:$\r$\n$\r$\n$_CLICK"
!endif

!ifdef UMUI_MULTILANGUAGEPAGE | UMUI_UNMULTILANGUAGEPAGE
  ${LangFileString} UMUI_TEXT_MULTILANGUAGE_LANGUAGE "Langage:"
!endif


!ifdef MUI_WELCOMEPAGE
  ${LangFileString} UMUI_TEXT_WELCOME_ALTERNATIVEINFO_TEXT "Vous �tes sur le point d'installer $(^NameDA) sur votre ordinateur.$\r$\n$\r$\n$_CLICK"
!endif


!ifdef UMUI_SERIALNUMBERPAGE | UMUI_UNSERIALNUMBERPAGE
  ${LangFileString} UMUI_TEXT_SERIALNUMBER_TITLE "Entrer votre num�ro de s�rie de $(^NameDA)"
  ${LangFileString} UMUI_TEXT_SERIALNUMBER_SUBTITLE "Veuillez renseigner les diff�rents champs ci-dessous."
  ${LangFileString} UMUI_TEXT_SERIALNUMBER_INFO_TEXT "Veuillez renseigner les diff�rents champs ci-dessous. $_CLICK"
  ${LangFileString} UMUI_TEXT_SERIALNUMBER_INVALIDATE_TEXT "$UMUI_SNTEXT invalide. Veuillez rev�rifier les informations que vous venez d'entrer."
  ${LangFileString} UMUI_TEXT_SERIALNUMBER_NAME "Nom"
  ${LangFileString} UMUI_TEXT_SERIALNUMBER_ORGANIZATION "Soci�t�"
  ${LangFileString} UMUI_TEXT_SERIALNUMBER_SERIALNUMBER "Num�ro de s�rie"
  ${LangFileString} UMUI_TEXT_SERIALNUMBER_ACTIVATIONCODE "Code d'activation"
  ${LangFileString} UMUI_TEXT_SERIALNUMBER_PASSWORD "Mot de passe"
!endif 


!ifdef UMUI_CONFIRMPAGE
  ${LangFileString} UMUI_TEXT_INSTCONFIRM_SUBTITLE "Le programme a finit de rassembler les informations et est pr�t � installer $(^NameDA)."
  ${LangFileString} UMUI_TEXT_INSTCONFIRM_TITLE "Confirmation de l'installation"
  ${LangFileString} UMUI_TEXT_INSTCONFIRM_TEXT_TOP "Le programme est pr�t � installer $(^NameDA) sur votre ordinateur.$\r$\nSi vous vouler revoir ou changer n'importe lequel de vos param�tres d'installation, cliquez sur Pr�c�dent. $_CLICK"
!endif 

!ifdef UMUI_UNCONFIRMPAGE
  ${LangFileString} UMUI_UNTEXT_INSTCONFIRM_SUBTITLE "Le programme a finit de rassembler les informations et est pr�t � d�sinstaller $(^NameDA)."
  ${LangFileString} UMUI_UNTEXT_INSTCONFIRM_TITLE "Confirmation de la d�sinstallation"
  ${LangFileString} UMUI_UNTEXT_INSTCONFIRM_TEXT_TOP "Le programme est pr�t � d�sinstaller $(^NameDA) sur votre ordinateur.$\r$\nSi vous vouler revoir ou changer n'importe lequel de vos param�tres de d�sinstallation, cliquez sur Pr�c�dent. Sinon cliquez sur Suivant pour commencer l'installation."
!endif 

!ifdef UMUI_CONFIRMPAGE | UMUI_UNCONFIRMPAGE
  ${LangFileString} UMUI_TEXT_INSTCONFIRM_TEXTBOX_TITLE "Configuration actuelle:"
  ${LangFileString} UMUI_TEXT_INSTCONFIRM_TEXTBOX_DESTINATION_LOCATION "Dossier de destination:"
  ${LangFileString} UMUI_TEXT_INSTCONFIRM_TEXTBOX_START_MENU_FOLDER "R�pertoire du menu d�marrer:"
  ${LangFileString} UMUI_TEXT_INSTCONFIRM_TEXTBOX_COMPNENTS "Les composants suivants seront install�s:"
!endif 


!ifdef UMUI_ABORTPAGE
  ${LangFileString} UMUI_TEXT_ABORT_INFO_TITLE "Abandon de l'installation de $(^NameDA)"
  ${LangFileString} UMUI_TEXT_ABORT_INFO_TEXT "L'installation a �t� interrompue avant que $(^NameDA) n'ait �t� compl�tement install�.$\r$\n$\r$\nPour installer ce programme plus tard, red�marrez l'installation une nouvelle fois.$\r$\n$\r$\n$\r$\n$\r$\nCliquez sur $(^CloseBtn) pour quitter le programme d'installation."
!endif 

!ifdef UMUI_UNABORTPAGE
  ${LangFileString} UMUI_UNTEXT_ABORT_INFO_TITLE "Abandon de la d�sinstallation de $(^NameDA)"
  ${LangFileString} UMUI_UNTEXT_ABORT_INFO_TEXT "La d�sinstallation a �t� interrompue avant que $(^NameDA) n'ait �t� compl�tement d�sinstall�.$\r$\n$\r$\nPour d�sinstaller ce programme plus tard, red�marrez la d�sinstallation une nouvelle fois.$\r$\n$\r$\n$\r$\n$\r$\nCliquez sur $(^CloseBtn) pour quitter le programme de d�sinstallation."
!endif


!ifdef UMUI_SETUPTYPEPAGE
  ${LangFileString} UMUI_TEXT_SETUPTYPE_TITLE "Type d'installation"
  ${LangFileString} UMUI_TEXT_SETUPTYPE_SUBTITLE "Choisissez le type d'installation qui convient le plus � vos besoins."
  ${LangFileString} UMUI_TEXT_SETUPTYPE_INFO_TEXT "Selectionnez un type d'installation."
  ${LangFileString} UMUI_TEXT_SETUPTYPE_MINIMAL_TEXT "Uniquement les fonctionnalit�s requise seront install�es. (Requiert le moins d'espace disque)"
  ${LangFileString} UMUI_TEXT_SETUPTYPE_STANDARD_TEXT "Toutes les principales fonctionnalit�s seront install�es. Recommand� pour la plupart des utilisateurs."
  ${LangFileString} UMUI_TEXT_SETUPTYPE_COMPLETE_TEXT "Toutes les fonctionnalit�s seront install�es. (Requiert le plus d'espace disque)"
  ${LangFileString} UMUI_TEXT_SETUPTYPE_CUSTOM_TEXT "Choisir quelles fonctionnalit�es du programme vous voulez installer et o� elles seront install�es. Recommand� pour les utilisateurs avanc�s."
!endif 

!ifdef UMUI_UNSETUPTYPEPAGE
  ${LangFileString} UMUI_UNTEXT_SETUPTYPE_TITLE "Type de D�sinstallation"
  ${LangFileString} UMUI_UNTEXT_SETUPTYPE_SUBTITLE "Choisissez le type de d�sinstallation qui convient le plus � vos besoins."
  ${LangFileString} UMUI_UNTEXT_SETUPTYPE_INFO_TEXT "Selectionnez un type de d�sinstallation."
  ${LangFileString} UMUI_UNTEXT_SETUPTYPE_MINIMAL_TEXT "Uniquement les pincipales fonctionnalit�s seront gard�s."
  ${LangFileString} UMUI_UNTEXT_SETUPTYPE_STANDARD_TEXT "Uniquement les fonctionnalit�s requises seront gard�s."
  ${LangFileString} UMUI_UNTEXT_SETUPTYPE_COMPLETE_TEXT "Tous le programme sera d�sinstall�."
  ${LangFileString} UMUI_UNTEXT_SETUPTYPE_CUSTOM_TEXT "Choisir quelles fonctionnalit�es du programme vous voulez d�sinstaller."
!endif 

!ifdef UMUI_SETUPTYPEPAGE | UMUI_UNSETUPTYPEPAGE
  ${LangFileString} UMUI_TEXT_SETUPTYPE_MINIMAL_TITLE "Minimale"
  ${LangFileString} UMUI_TEXT_SETUPTYPE_STANDARD_TITLE "Standard"
  ${LangFileString} UMUI_TEXT_SETUPTYPE_COMPLETE_TITLE "Compl�te"
  ${LangFileString} UMUI_TEXT_SETUPTYPE_CUSTOM_TITLE "Personnalis�e"
!endif 


!ifdef UMUI_INFORMATIONPAGE
  ${LangFileString} UMUI_TEXT_INFORMATION_SUBTITLE "Veuillez prendre connaissance des informations concernant l'installation de $(^NameDA)."
!endif 

!ifdef UMUI_UNINFORMATIONPAGE
  ${LangFileString} UMUI_UNTEXT_INFORMATION_SUBTITLE "Veuillez prendre connaissance des informations concernant la d�sinstallation de $(^NameDA)."
!endif 

!ifdef UMUI_INFORMATIONPAGE | UMUI_UNINFORMATIONPAGE
  ${LangFileString} UMUI_TEXT_INFORMATION_TITLE "Information"
  ${LangFileString} UMUI_TEXT_INFORMATION_INFO_TEXT "Informations concernant $(^NameDA)."
!endif 


!ifdef UMUI_ADDITIONALTASKSPAGE
  ${LangFileString} UMUI_TEXT_ADDITIONALTASKS_INFO_TEXT "S�lectionnez les t�ches suppl�mentaires que l'assistant doit effectuer pendant l'installation de $(^NameDA). $_CLICK"
!endif 

!ifdef UMUI_UNADDITIONALTASKSPAGE
  ${LangFileString} UMUI_UNTEXT_ADDITIONALTASKS_INFO_TEXT "S�lectionnez les t�ches suppl�mentaires que l'assistant doit effectuer pendant la d�sinstallation de $(^NameDA). $_CLICK"
!endif 

!ifdef UMUI_ADDITIONALTASKSPAGE | UMUI_UNADDITIONALTASKSPAGE
  ${LangFileString} UMUI_TEXT_ADDITIONALTASKS_TITLE "T�ches suppl�mentaires"
  ${LangFileString} UMUI_TEXT_ADDITIONALTASKS_SUBTITLE "Quelles sont les t�ches suppl�mentaires qui doivent �tre effectu�es?"
  ${LangFileString} UMUI_TEXT_ADDITIONALTASKS_ADDITIONAL_ICONS "Ic�nes Additionnelles:"
  ${LangFileString} UMUI_TEXT_ADDITIONALTASKS_CREATE_DESKTOP_ICON "Cr�er une ic�ne de bureau"
  ${LangFileString} UMUI_TEXT_ADDITIONALTASKS_CREATE_QUICK_LAUNCH_ICON "Cr�er une ic�ne de lancement rapide"
  ${LangFileString} UMUI_TEXT_ADDITIONALTASKS_ADVANCED_PARAMETERS "Param�tres avanc�s:"
  ${LangFileString} UMUI_TEXT_ADDITIONALTASKS_LAUNCH_PROGRAM_AT_WINDOWS_STARTUP "Lancer $(^NameDA) au d�marrage de Windows"
  ${LangFileString} UMUI_TEXT_ADDITIONALTASKS_FILE_ASSOCIATION "Association de fichier:"
  ${LangFileString} UMUI_TEXT_ADDITIONALTASKS_ASSOCIATE_WITH "Associer $(^NameDA) avec les fichiers de type "
  ${LangFileString} UMUI_TEXT_ADDITIONALTASKS_ASSOCIATE_WITH_END ""
!endif 
  
  
!ifdef UMUI_CONFIRMPAGE | UMUI_UNCONFIRMPAGE | UMUI_ALTERNATIVESTARTMENUPAGE | UMUI_UNALTERNATIVESTARTMENUPAGE
  ${LangFileString} UMUI_TEXT_SHELL_VAR_CONTEXT "Comment les raccourcis seront cr��s:"
  ${LangFileString} UMUI_TEXT_SHELL_VAR_CONTEXT_FOR_ALL_USERS "Pour tous les utilisateurs"
  ${LangFileString} UMUI_TEXT_SHELL_VAR_CONTEXT_ONLY_FOR_CURRENT_USER "Uniquement pour l'utilisateur courant"
!endif


!ifdef UMUI_UPDATEPAGE
  ${LangFileString} UMUI_TEXT_UPDATE_TITLE "Mise � jour"
  ${LangFileString} UMUI_TEXT_UPDATE_SUBTITLE "Mettre � jour une ancienne version du programme."
  ${LangFileString} UMUI_TEXT_UPDATE_INFO_TEXT "Bienvenue dans l'assistant de mise � jour de $(^NameDA).$\n$\rCe programme va vous permettre de mettre � jour la version $OLDVERSION qui a �t� trouv� sur votre ordinateur."
  ${LangFileString} UMUI_TEXT_UPDATE_UPDATE_TITLE "Mettre � jour"
  ${LangFileString} UMUI_TEXT_UPDATE_UPDATE_TEXT "Mettre � jour tous les composants de $(^NameDA) d�j� install�s � la version $NEWVERSION."
  ${LangFileString} UMUI_TEXT_UPDATE_REMOVE_TITLE "Supprimer"
  ${LangFileString} UMUI_TEXT_UPDATE_REMOVE_TEXT "D�sinstaller $(^NameDA) de votre ordinateur."
  ${LangFileString} UMUI_TEXT_UPDATE_CONTINUE_TITLE "Continuer l'installation"
  ${LangFileString} UMUI_TEXT_UPDATE_CONTINUE_TEXT "Continuer l'installation comme d'habitude. Utilisez cette option si vous voulez installer cette nouvelle version dans un autre r�pertoire en parall�le � la version pr�c�dente."
!endif


!ifdef UMUI_MAINTENANCEPAGE | UMUI_UNMAINTENANCEPAGE
  ${LangFileString} UMUI_TEXT_MAINTENANCE_TITLE "Maintenance"
  ${LangFileString} UMUI_TEXT_MAINTENANCE_SUBTITLE "Modifier, r�parer, ou supprimer le programme."
  ${LangFileString} UMUI_TEXT_MAINTENANCE_INFO_TEXT "Bienvenue dans l'assistant de maintenance de $(^NameDA).$\r$\nCe programme va vous permettre de modifier l'installation actuelle."
  ${LangFileString} UMUI_TEXT_MAINTENANCE_MODIFY_TITLE "Modifier"
  ${LangFileString} UMUI_TEXT_MAINTENANCE_MODIFY_TEXT "S�lectionner de nouveaux composants � ajouter et s�lectionner des composants d�j� install�s � supprimer."
  ${LangFileString} UMUI_TEXT_MAINTENANCE_REPAIR_TITLE "R�parer"
  ${LangFileString} UMUI_TEXT_MAINTENANCE_REPAIR_TEXT "R�installer tous les composants de $(^NameDA) d�j� install�s."
  ${LangFileString} UMUI_TEXT_MAINTENANCE_REMOVE_TITLE "Supprimer"
  ${LangFileString} UMUI_TEXT_MAINTENANCE_REMOVE_TEXT "D�sinstaller $(^NameDA) de votre ordinateur."
  ${LangFileString} UMUI_TEXT_MAINTENANCE_CONTINUE_TITLE "Continuer l'installation"
  ${LangFileString} UMUI_TEXT_MAINTENANCE_CONTINUE_TEXT "Continuer l'installation comme d'habitude. Pour r�installer ce programme sur une pr�c�dente installation ou une nouvelle fois dans un autre r�pertoire."
!endif


!ifdef UMUI_FILEDISKREQUESTPAGE | UMUI_UNFILEDISKREQUESTPAGE
  ${LangFileString} UMUI_TEXT_FILEDISKREQUEST_FILE_SUBTITLE_BEGIN "L'assistant d'installation � besoin du fichier"
  ${LangFileString} UMUI_TEXT_FILEDISKREQUEST_FILE_SUBTITLE_END "pour continuer."
  ${LangFileString} UMUI_TEXT_FILEDISKREQUEST_DISK_SUBTITLE "L'assistant d'installation � besoin du disque suivant pour continuer."
  ${LangFileString} UMUI_TEXT_FILEDISKREQUEST_FILE_BEGIN "Sp�cifier la localisation du fichier"
  ${LangFileString} UMUI_TEXT_FILEDISKREQUEST_FILE_END "pour continuer."
  ${LangFileString} UMUI_TEXT_FILEDISKREQUEST_DISK "Veuillez ins�rer le"
  ${LangFileString} UMUI_TEXT_FILEDISKREQUEST_PATH "Chemin:"
!endif
