專題名稱：安專ˊ呷藥啊

校名與科系：中央大學資訊管理學系 

團員成員：林俊霆、王柏勛、許珀維、蘇湘婷、賴思妤

![project_drug_5](https://github.com/JunTingLin/drug_android_frontend/assets/92431095/7c668ec4-4204-4317-b1fc-2f61ff8ab081)


⚠️ 注意：此倉庫僅有前端代碼，後端請參考[medicinehelper](https://github.com/brankhsu/medicinehelper)⚠️

## 1. 前言

「藥能治病，也能致病。」要透過藥品改善疾病症狀，正確使用藥品很重要。市面上有很多智慧藥盒以及藥物管理APP流通，它們皆具備基本藥物管理功能，然而卻需要手動設定服藥時間和次數，有些藥盒的設計甚至可能導致誤食藥物，且所有產品皆缺乏藥物交互作用檢查。為了改善現存產品功能，我們建立一款結合智慧藥盒的手機 APP。使用者可以利用藥袋拍攝功能，自動紀錄藥袋資訊，並且藥盒會根據藥袋上的醫囑，於設定的時間自動開啟，減少混淆和誤食其他藥物的風險。此外，系統會對藥物進行交互作用檢查，提升便利性的同時，也確保使用者的用藥安全。

## 2. 創意描述

本專案除了包含市面上的服藥APP以及智慧藥盒所包含的功能，更進一步優化其他系統不足之處。
首先，改善藥袋辨識功能不能廣泛使用的缺點，提升使用者輸入藥袋資訊的便捷性。另外，針對使用者的在服藥物進行交互作用比對，提升使用者服藥的安全性。最後，利用智慧藥盒配合醫囑資訊的管理，致力於減少使用者錯誤用藥的情形發生。
![image](https://github.com/JunTingLin/drug_android_frontend/assets/92431095/c69eb193-cd6d-49e8-ac51-a6720d5dcf01)
圖 1 相關 App 功能比較表

## 3. 系統功能簡介 
以下依據APP呈現的四個介面做功能介紹。
+ 吃藥提醒介面：
  + 使用者可以於此頁面檢視當日應服用的藥物，紅標的藥物為須於當時段服用或是超過時間但仍未服用的藥物；綠標的藥物則是當日需服用但時段未到的藥物。
  + 本專案透過智慧手機訊息與鈴聲通知，以提醒使用者服藥，當使用者選擇服用藥物，對應該藥物的格子便會自動開啟。
  + 另外設有「新增單筆紀錄」的功能，提供如止痛藥或抗組織胺藥物等臨時服用的新增需求。
  + 使用者可以針對單筆服藥提醒，進行左滑或右滑的操作，服用或過藥物，若是到凌晨十二點整，使用者都沒有對該藥物進行動作，系統就會將該藥物的服用狀態設為未知。使用者也可以於介面上方的橫桿選取某一個時段，一次開啟多個藥格。
+ 藥物紀錄管理介面：
  + 使用者可以透過這個介面管理藥物紀錄，與藥物有關的資訊，諸如藥物服用時機、頻率或是服用提醒，都可以在此介面進行設定。
  + 使用者也可以透過此介面新增藥物，而新增藥物的方式則可分為光學文字辨識或是文字輸入的方式。而在正式新增藥物之前，系統將透過網路爬蟲自動比對該藥物與使用者的在服藥物是否有交互作用。
+ 藥物服用紀錄介面：
  + 此介面記錄使用者的服藥狀況，使用者或是醫師可以透過此介面了解藥物的服用情形。使用者依據不同需求選擇「依時間」或是「依藥物」分頁，透過「依時間」分頁，使用者可以查看同一時段或是鄰近時段所服用的藥物，而透過「依藥物」分頁，使用者則可以檢視該藥物的服用狀態。
+ 智慧藥盒管理介面：
  + 此介面顯示目前藥盒中藥物的擺放分佈，使用者可以於此介面修改藥物擺放位置或移除某藥物，也可以進行新增藥物的操作。

![image](https://github.com/JunTingLin/drug_android_frontend/assets/92431095/48cc86e3-b9fa-4f95-86d7-4c19457e133b)

圖 2 系統功能樹狀圖

## 4. 系統特色
+ 交互作用比照
  + 本系統在藥物置入之前會自動比對該藥物與使用者在服藥使否產生交互作用，為使用者的用藥安全增添保障。資料取得方式透過即時爬蟲、緩存資料庫等，既全面也透過暫存資料庫減少等待時間。
+ 系統功能完善
  + 系統功能完善，從置入藥物到服藥結束前的回診提醒流程一應俱全，還有各種防呆設計。除了APP上的幫助，更結合實體藥盒，便於藥物的服用。
+ 頁面設計友善
  + 頁面設計直覺化、視覺化，且便於使用，例如在「吃藥提醒介面」使用者可以左右滑動，達到刪除或服用的效果，另外使用者可以自行調整字體大小、語言、介面色調來貼合自己的需求。
+ 藥盒安全管制
  + 由於藥盒的開關是由APP控制，可以避免長輩因為不確定的外力因素，誤食或錯誤服用到具交互作用的藥物，另外，分隔的藥格也能避免同時開啟整個藥盒的可能會產生的混淆。
 
![image](https://github.com/JunTingLin/drug_android_frontend/assets/92431095/ae774893-7e4b-4384-8f3e-0669db778b06)

圖 3 APP前端功能截圖

![image](https://github.com/JunTingLin/drug_android_frontend/assets/92431095/5b1b03aa-815d-4bbc-bc26-85bb0eb56adb)
圖 4 獨立開閉之智慧藥盒雛形

## 5. 系統開發工具與技術
圖 5為整個系統的架構圖，使用者端可透過 App 介面實行藥物輸入、藥袋藥品的拍攝等操作，經由後端處理回調結果給使用者透過爬蟲取得藥品資料、交互資料後存入資料庫，並透過 App 去控制IoT 智慧藥盒。 
+ 前端：APP介面
  + 使用Android studio開發環境，清爽的Kotlin語言編寫。介面設計使用Google Design的Material Design 3元件，並使用Firebase處理手機的推播通知。
+ 後端：OCR藥袋辨識模組、爬蟲程式模組
  + 後端的APP API Gateway我們使用Django框架，並且將後端Application Server環境架設在Pythonanywhere的平台上面。
    1. OCR 輸入辨識模組：透過 AndroidX Camera 核心庫，我們高效地讀取圖像。接著使用 Google Cloud Platform 的 Cloud Vision API 進行圖像區分和文字擷取。獲得的文字資料經由 OpenAI 的 ChatGPT 進行關鍵字提取。最後，與後台資料庫進行模糊比對，確保提取的藥名準確性。
    2. 爬蟲程式模組：爬蟲程式模組負責藥物交互作用的比對，使用的是Selenium和Beautiful Soup套件。我們的資料來源是具有公信力的藥物線上資料集Drugs.com。且我們後台資料庫設有緩存機制，當資料庫沒有這筆藥物紀錄時，才會進行即時性的爬取。

![image](https://github.com/JunTingLin/drug_android_frontend/assets/92431095/15d50ae8-854b-4437-8778-be949be7d5be)

圖 5 系統架構圖

![image](https://github.com/JunTingLin/drug_android_frontend/assets/92431095/acffd0ea-4ccc-40f4-811c-d315773fecb4)

圖 6 CLOUD VISION API，上傳藥袋封面，辨識區塊文字並回傳Json

## 6. 系統使用對象 
65歲以上的年長者、獨居老人、先天性疾病患者，或長期服藥者。
## 7. 系統使用環境 
能連接網路並具備前置鏡頭的Android系統手機或平板。
## 8. 結語
我們致力於提供最完善的藥物管理APP，藉由光學文字辨識擷取藥袋上的藥物資訊，以及即時將藥物與在服藥物進行交互作用比對，讓使用者能以簡單、便捷的方式進行藥物管理，更結合智慧藥盒，期望保障長者或需要長期服用多種藥物的使用者之服藥安全。






