;NSIS Modern User Interface - Language File
;Compatible with UltraModernUI 1.00 beta 2

;Language: Japanese (1041)
;By Logue <http://logue.be/>
;modified 2009/06/23

;--------------------------------

!ifdef UMUI_MULTILANGUAGEPAGE
  ${LangFileString} UMUI_TEXT_MULTILANGUAGE_TITLE "$(^NameDA)�̃Z�b�g�A�b�v�ւ悤����"
  ${LangFileString} UMUI_TEXT_MULTILANGUAGE_TEXT "$(^NameDA)���C���X�g�[������O�Ɍ����I�����Ă��������F$\r$\n$\r$\n$_CLICK"
!endif

!ifdef UMUI_UNMULTILANGUAGEPAGE
  ${LangFileString} UMUI_UNTEXT_MULTILANGUAGE_TITLE "$(^NameDA)�̃A���C���X�g�[���E�B�U�[�h�ւ悤����"
  ${LangFileString} UMUI_UNTEXT_MULTILANGUAGE_TEXT "$(^NameDA)���A���C���X�g�[������O�Ɍ����I�����Ă��������F$\r$\n$\r$\n$_CLICK"
!endif

!ifdef UMUI_MULTILANGUAGEPAGE | UMUI_UNMULTILANGUAGEPAGE
  ${LangFileString} UMUI_TEXT_MULTILANGUAGE_LANGUAGE "����F"
!endif


!ifdef MUI_WELCOMEPAGE
  ${LangFileString} UMUI_TEXT_WELCOME_ALTERNATIVEINFO_TEXT "���̃E�B�U�[�h�́A$(^NameDA)���C���X�g�[�������ł̃K�C�h���s���܂��B$\r$\n$\r$\n$\r$\n$_CLICK"
!endif


!ifdef UMUI_SERIALNUMBERPAGE | UMUI_UNSERIALNUMBERPAGE
  ${LangFileString} UMUI_TEXT_SERIALNUMBER_TITLE "$(^NameDA)�̃V���A���i���o�[����͂��Ă��������B"
  ${LangFileString} UMUI_TEXT_SERIALNUMBER_SUBTITLE "�ȉ��̍��ڂ���͂��Ă��������B"
  ${LangFileString} UMUI_TEXT_SERIALNUMBER_INFO_TEXT "�ȉ��̍��ڂ���͂��Ă��������B$_CLICK"
  ${LangFileString} UMUI_TEXT_SERIALNUMBER_INVALIDATE_TEXT "$UMUI_SNTEXT�̒l���s���ł��B���萔�ł������͓��e�����m�F���������B"
  ${LangFileString} UMUI_TEXT_SERIALNUMBER_NAME "���O"
  ${LangFileString} UMUI_TEXT_SERIALNUMBER_ORGANIZATION "����"
  ${LangFileString} UMUI_TEXT_SERIALNUMBER_SERIALNUMBER "�V���A���i���o�["
  ${LangFileString} UMUI_TEXT_SERIALNUMBER_ACTIVATIONCODE "�A�N�e�B�x�[�V�����R�[�h"
  ${LangFileString} UMUI_TEXT_SERIALNUMBER_PASSWORD "�p�X���[�h"
!endif 


!ifdef UMUI_CONFIRMPAGE
  ${LangFileString} UMUI_TEXT_INSTCONFIRM_SUBTITLE "�Z�b�g�A�b�v�́A$(^NameDA)���C���X�g�[�������ł̕K�v�ȏ��̎��W����сA�������������܂����B"
  ${LangFileString} UMUI_TEXT_INSTCONFIRM_TITLE "�C���X�g�[���̊m�F"
  ${LangFileString} UMUI_TEXT_INSTCONFIRM_TEXT_TOP "�Z�b�g�A�b�v�́A���Ȃ��̃R���s���[�^�[��$(^NameDA)���C���X�g�[�����鏀�����ł��܂����B$\r$\n�������A�C���X�g�[���ݒ��ύX����K�v������ꍇ�́A�u�߂�v�{�^���������Ă��������B$_CLICK"
!endif 

!ifdef UMUI_UNCONFIRMPAGE
  ${LangFileString} UMUI_UNTEXT_INSTCONFIRM_SUBTITLE "�Z�b�g�A�b�v�́A$(^NameDA)���A���C���X�g�[�������ł̕K�v�ȏ��̎��W����сA�������������܂����B"
  ${LangFileString} UMUI_UNTEXT_INSTCONFIRM_TITLE "�A���C���X�g�[���̊m�F"
  ${LangFileString} UMUI_UNTEXT_INSTCONFIRM_TEXT_TOP "�Z�b�g�A�b�v�́A���Ȃ��̃R���s���[�^�[����$(^NameDA)���A���C���X�g�[�����鏀�����ł��܂����B$\r$\n�������A�A���C���X�g�[���ݒ��ύX����K�v������ꍇ�́A�u�߂�v�{�^���������Ă��������B�u���ցv�{�^�����N���b�N����ƃA���C���X�g�[�����J�n���܂��B"
!endif 

!ifdef UMUI_CONFIRMPAGE | UMUI_UNCONFIRMPAGE
  ${LangFileString} UMUI_TEXT_INSTCONFIRM_TEXTBOX_TITLE "���݂̐ݒ�F"
  ${LangFileString} UMUI_TEXT_INSTCONFIRM_TEXTBOX_DESTINATION_LOCATION "�C���X�g�[����F"
  ${LangFileString} UMUI_TEXT_INSTCONFIRM_TEXTBOX_START_MENU_FOLDER "�X�^�[�g���j���[�t�H���_�F"
  ${LangFileString} UMUI_TEXT_INSTCONFIRM_TEXTBOX_COMPNENTS "�C���X�g�[�������R���|�[�l���g�F"
!endif 


!ifdef UMUI_ABORTPAGE
  ${LangFileString} UMUI_TEXT_ABORT_INFO_TITLE "$(^NameDA)�Z�b�g�A�b�v�E�B�U�[�h�̒��f"
  ${LangFileString} UMUI_TEXT_ABORT_INFO_TEXT "�E�B�U�[�h�́A$(^NameDA)�̃C���X�g�[����Ƃ𒆒f���܂����B$\r$\n$\r$\n���̃v���O�������C���X�g�[�����邽�߂ɂ́A���ƂŃZ�b�g�A�b�v���Ď��s����K�v������܂��B$\r$\n$\r$\n$\r$\n$\r$\n�u$(^CloseBtn)�v�{�^���������ƃZ�b�g�A�b�v�E�B�U�[�h���I�����܂��B"
!endif 

!ifdef UMUI_UNABORTPAGE
  ${LangFileString} UMUI_UNTEXT_ABORT_INFO_TITLE "$(^NameDA)�A���C���X�g�[���E�B�U�[�h�̒��f"
  ${LangFileString} UMUI_UNTEXT_ABORT_INFO_TEXT "�E�B�U�[�h�́A$(^NameDA)�̃A���C���X�g�[����Ƃ𒆒f���܂����B$\r$\n$\r$\n���̃v���O�������A���C���X�g�[�����邽�߂ɂ́A���ƂŃA���C���X�g�[�����Ď��s����K�v������܂��B$\r$\n$\r$\n$\r$\n$\r$\n�u$(^CloseBtn)�v�{�^���������ƃA���C���X�g�[���E�B�U�[�h���I�����܂��B"
!endif


!ifdef UMUI_SETUPTYPEPAGE
  ${LangFileString} UMUI_TEXT_SETUPTYPE_TITLE "�Z�b�g�A�b�v�^�C�v"
  ${LangFileString} UMUI_TEXT_SETUPTYPE_SUBTITLE "�K�v�ȍ��ڂ�I�����Ă��������B"
  ${LangFileString} UMUI_TEXT_SETUPTYPE_INFO_TEXT "�Z�b�g�A�b�v�^�C�v��I�����Ă��������B"
  ${LangFileString} UMUI_TEXT_SETUPTYPE_MINIMAL_TEXT "�Œ���K�v�ȋ@�\�̂݁i�f�B�X�N����ʂ��ŏ����ł��j"
  ${LangFileString} UMUI_TEXT_SETUPTYPE_STANDARD_TEXT "���C���ƂȂ�@�\���C���X�g�[������܂��B�����̃��[�U�ɐ�������܂��B"
  ${LangFileString} UMUI_TEXT_SETUPTYPE_COMPLETE_TEXT "���ׂĂ̋@�\���C���X�g�[������܂��B�i�f�B�X�N�̏���ʂ��ő�ł��j"
  ${LangFileString} UMUI_TEXT_SETUPTYPE_CUSTOM_TEXT "�v���O�����ɕK�v�ȋ@�\�������őI���ł��܂��B�㋉���[�U�����ł��B"
!endif 

!ifdef UMUI_UNSETUPTYPEPAGE
  ${LangFileString} UMUI_UNTEXT_SETUPTYPE_TITLE "�A���C���X�g�[���^�C�v"
  ${LangFileString} UMUI_UNTEXT_SETUPTYPE_SUBTITLE "�A���C���X�g�[�����������ڂ�I�����Ă��������B"
  ${LangFileString} UMUI_UNTEXT_SETUPTYPE_INFO_TEXT "�A���C���X�g�[���^�C�v��I�����Ă��������B"
  ${LangFileString} UMUI_UNTEXT_SETUPTYPE_MINIMAL_TEXT "���C���ƂȂ�@�\�͎c��܂��B"
  ${LangFileString} UMUI_UNTEXT_SETUPTYPE_STANDARD_TEXT "�K�v�ȋ@�\�͎c��܂��B"
  ${LangFileString} UMUI_UNTEXT_SETUPTYPE_COMPLETE_TEXT "���ׂẴv���O�������A���C���X�g�[�����܂��B"
  ${LangFileString} UMUI_UNTEXT_SETUPTYPE_CUSTOM_TEXT "�A���C���X�g�[������@�\�������őI�����܂��B"
!endif 

!ifdef UMUI_SETUPTYPEPAGE | UMUI_UNSETUPTYPEPAGE
  ${LangFileString} UMUI_TEXT_SETUPTYPE_MINIMAL_TITLE "�ŏ���"
  ${LangFileString} UMUI_TEXT_SETUPTYPE_STANDARD_TITLE "���"
  ${LangFileString} UMUI_TEXT_SETUPTYPE_COMPLETE_TITLE "���S"
  ${LangFileString} UMUI_TEXT_SETUPTYPE_CUSTOM_TITLE "�J�X�^��"
!endif 


!ifdef UMUI_INFORMATIONPAGE
  ${LangFileString} UMUI_TEXT_INFORMATION_SUBTITLE "$(^NameDA)���C���X�g�[�������ł̊֘A�������m�F���������B"
!endif 

!ifdef UMUI_UNINFORMATIONPAGE
  ${LangFileString} UMUI_UNTEXT_INFORMATION_SUBTITLE "$(^NameDA)���A���C���X�g�[�������ł̊֘A�������m�F���������B"
!endif 

!ifdef UMUI_INFORMATIONPAGE | UMUI_UNINFORMATIONPAGE
  ${LangFileString} UMUI_TEXT_INFORMATION_TITLE "���"
  ${LangFileString} UMUI_TEXT_INFORMATION_INFO_TEXT "$(^NameDA)�̊֘A���ł��B"
!endif 


!ifdef UMUI_ADDITIONALTASKSPAGE
  ${LangFileString} UMUI_TEXT_ADDITIONALTASKS_INFO_TEXT "$(^NameDA)�̃Z�b�g�A�b�v��Ǝ��ɍs�������ǉ��̃^�X�N��I�����Ă��������B$_CLICK"
!endif 

!ifdef UMUI_UNADDITIONALTASKSPAGE
  ${LangFileString} UMUI_UNTEXT_ADDITIONALTASKS_INFO_TEXT "$(^NameDA)�̃A���X�g�[����Ǝ��ɍs�������ǉ��̃^�X�N��I�����Ă�������$_CLICK"
!endif 

!ifdef UMUI_ADDITIONALTASKSPAGE | UMUI_UNADDITIONALTASKSPAGE
  ${LangFileString} UMUI_TEXT_ADDITIONALTASKS_TITLE "�ǉ�������"
  ${LangFileString} UMUI_TEXT_ADDITIONALTASKS_SUBTITLE "�ǉ��̃^�X�N��I�����Ă��������B"
  ${LangFileString} UMUI_TEXT_ADDITIONALTASKS_ADDITIONAL_ICONS "�g���A�C�R���F"
  ${LangFileString} UMUI_TEXT_ADDITIONALTASKS_CREATE_DESKTOP_ICON "�f�B�X�N�g�b�v�ɍ쐬"
  ${LangFileString} UMUI_TEXT_ADDITIONALTASKS_CREATE_QUICK_LAUNCH_ICON "�N�C�b�N�N���ɍ쐬"
  ${LangFileString} UMUI_TEXT_ADDITIONALTASKS_ADVANCED_PARAMETERS "�g���p�����[�^�[�F"
  ${LangFileString} UMUI_TEXT_ADDITIONALTASKS_LAUNCH_PROGRAM_AT_WINDOWS_STARTUP "Windows�̃X�^�[�g�A�b�v���Ɏ��s����$(^NameDA)"
  ${LangFileString} UMUI_TEXT_ADDITIONALTASKS_FILE_ASSOCIATION "�t�@�C���̊֘A�Â��F"
  ${LangFileString} UMUI_TEXT_ADDITIONALTASKS_ASSOCIATE_WITH "$(^NameDA)�Ɗ֘A�Â���t�@�C���^�C�v"
  ${LangFileString} UMUI_TEXT_ADDITIONALTASKS_ASSOCIATE_WITH_END ""
!endif 
  
  
!ifdef UMUI_CONFIRMPAGE | UMUI_UNCONFIRMPAGE | UMUI_ALTERNATIVESTARTMENUPAGE | UMUI_UNALTERNATIVESTARTMENUPAGE
  ${LangFileString} UMUI_TEXT_SHELL_VAR_CONTEXT "�쐬����V���[�g�J�b�g�F"
  ${LangFileString} UMUI_TEXT_SHELL_VAR_CONTEXT_FOR_ALL_USERS "���ׂẴ��[�U"
  ${LangFileString} UMUI_TEXT_SHELL_VAR_CONTEXT_ONLY_FOR_CURRENT_USER "���݂̃��[�U�̂�"
!endif


!ifdef UMUI_UPDATEPAGE
  ${LangFileString} UMUI_TEXT_UPDATE_TITLE "�A�b�v�f�[�g"
  ${LangFileString} UMUI_TEXT_UPDATE_SUBTITLE "�O�̃o�[�W�����̃v���O�������A�b�v�f�[�g���܂��B"
  ${LangFileString} UMUI_TEXT_UPDATE_INFO_TEXT "$(^NameDA)�A�b�v�f�[�g�E�B�U�[�h�ւ悤�����B$\r$\n���̃v���O�����́A�R���s���[�^�[�ɃC���X�g�[������Ă���$OLDVERSION���A�b�v�f�[�g���܂��B"
  ${LangFileString} UMUI_TEXT_UPDATE_UPDATE_TITLE "�A�b�v�f�[�g"
  ${LangFileString} UMUI_TEXT_UPDATE_UPDATE_TEXT "�C���X�g�[������Ă���S�Ă�$(^NameDA)�̃R���|�[�l���g��$NEWVERSION�ɃA�b�v�f�[�g���Ă��܂��c"
  ${LangFileString} UMUI_TEXT_UPDATE_REMOVE_TITLE "�폜"
  ${LangFileString} UMUI_TEXT_UPDATE_REMOVE_TEXT "$(^NameDA)�����Ȃ��̃R���s���[�^�[����A���C���X�g�[�����܂��B"
  ${LangFileString} UMUI_TEXT_UPDATE_CONTINUE_TITLE "�Z�b�g�A�b�v���s"
  ${LangFileString} UMUI_TEXT_UPDATE_CONTINUE_TEXT "�ʏ�ʂ�Z�b�g�A�b�v�𑱍s���Ă��������B���̃I�v�V�����́A�V�����o�[�W�����̃v���O�����ɃA�b�v�f�[�g������A�ʂ̃t�H���_�ɃC���X�g�[���������ꍇ�g�p���܂��B"
!endif


!ifdef UMUI_MAINTENANCEPAGE | UMUI_UNMAINTENANCEPAGE
  ${LangFileString} UMUI_TEXT_MAINTENANCE_TITLE "�����e�i���X"
  ${LangFileString} UMUI_TEXT_MAINTENANCE_SUBTITLE "�v���O������ύX�A�C���܂��͍폜���܂��B"
  ${LangFileString} UMUI_TEXT_MAINTENANCE_INFO_TEXT "$(^NameDA)�Z�b�g�A�b�v�����e�i���X�v���O�����ւ悤�����B$\r$\n���̃v���O�����́A���݂̃C���X�g�[�����C�����邱�Ƃ��ł��܂��B"
  ${LangFileString} UMUI_TEXT_MAINTENANCE_MODIFY_TITLE "�ύX"
  ${LangFileString} UMUI_TEXT_MAINTENANCE_MODIFY_TEXT "�V�����R���|�[�l���g��ǉ�������A�C���X�g�[������Ă��鍀�ڂ��폜�����肷�邱�Ƃ��ł��܂��B"
  ${LangFileString} UMUI_TEXT_MAINTENANCE_REPAIR_TITLE "�C��"
  ${LangFileString} UMUI_TEXT_MAINTENANCE_REPAIR_TEXT "$(^NameDA)�̃R���|�[�l���g���ăC���X�g�[�����܂��B"
  ${LangFileString} UMUI_TEXT_MAINTENANCE_REMOVE_TITLE "�폜"
  ${LangFileString} UMUI_TEXT_MAINTENANCE_REMOVE_TEXT "$(^NameDA)���A���C���X�g�[�����܂�"
  ${LangFileString} UMUI_TEXT_MAINTENANCE_CONTINUE_TITLE "�Z�b�g�A�b�v���s"
  ${LangFileString} UMUI_TEXT_MAINTENANCE_CONTINUE_TEXT "�ʏ�ʂ�Z�b�g�A�b�v�𑱍s���Ă��������B���̃I�v�V�����́A�����̃v���O�������ăC���X�g�[��������A�قȂ�t�H���_�ɃC���X�g�[���������ꍇ�g�p���܂��B"
!endif


!ifdef UMUI_FILEDISKREQUESTPAGE | UMUI_UNFILEDISKREQUESTPAGE
  ${LangFileString} UMUI_TEXT_FILEDISKREQUEST_FILE_SUBTITLE_BEGIN "�Z�b�g�A�b�v�𑱍s����ɂ́A"
  ${LangFileString} UMUI_TEXT_FILEDISKREQUEST_FILE_SUBTITLE_END "�t�@�C�����K�v�ł��B"
  ${LangFileString} UMUI_TEXT_FILEDISKREQUEST_DISK_SUBTITLE "�Z�b�g�A�b�v�𑱍s����ɂ́A���̃f�B�X�N��}�����Ă��������B"
  ${LangFileString} UMUI_TEXT_FILEDISKREQUEST_FILE_BEGIN "������ɂ́A"
  ${LangFileString} UMUI_TEXT_FILEDISKREQUEST_FILE_END "�t�@�C�����w�肵�Ă��������B"
  ${LangFileString} UMUI_TEXT_FILEDISKREQUEST_DISK ""
  ${LangFileString} UMUI_TEXT_FILEDISKREQUEST_PATH "�̃p�X����͂��Ă��������F"
!endif
