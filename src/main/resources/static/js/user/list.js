'usestrict';

var userData=null;//ユーザーデータ
var table=null;//DataTablesオブジェクト

/**画面ロード時の処理.*/
jQuery(function($){
    
    //DataTablesの初期化
    createDataTables();
    
    /**検索ボタンを押したときの処理.*/
    $('#btn-search').click(function(event){
        //検索
        search();
    });

    /** ダウンロードボタン(JavaScript)の押下時イベント. */
    $('#download-java-script').click(function() {
        //通常のアクションをキャンセル
        event.preventDefault();

        var xhr = new XMLHttpRequest();
        // リクエスト先
        xhr.open('POST', '/user/list/download', true);
        // レスポンスタイプ
        xhr.responseType = 'blob';
        // リクエスト成功時の処理を定義
        xhr.onload = function(e) {
            // ファイル名
            var fileName = 'userListJavaScript.csv';
            // ファイル保存関数の呼び出し
            fileSave(this.response, fileName);
        };
        
        // CSRF対策
        var token = $("input[name='_csrf']").val();
        var header = "X-CSRF-TOKEN";
        xhr.setRequestHeader(header, token);
        
        // リクエスト実行
        xhr.send();
    });
    
    /** ダウンロードボタン(jQuery)の押下時イベント. */
    $('#download-jquery').click(function() {
        //通常のアクションをキャンセル
        event.preventDefault();
    
        // フォームの値を取得(CSRF対策)
        var formData = $('#download-form').serializeArray();
    
        // ajaxでファイルダウンロード
        $.ajax({
            type: 'post',
            url: '/user/list/download',
            data: formData,
            xhrFields:{
            responseType: 'blob'
            },
        })
        // ajax成功時の処理
        .done(function( data, status, jqXHR ) {
            // ファイル名
            var fileName = 'userListJQuery.csv';
        
            // Blob作成
            const blob = new Blob([data], {type: data.type});

            // ファイル保存関数の呼び出し
            fileSave(data, fileName);
        })
        // ajax失敗時の処理
        .fail(function( jqXHR, status, errorThrown ) {
            alert('ファイルダウンロード失敗');
        })
        // 成功しても失敗しても実行する処理
        .always(function( data, status, errorThrown ) {
            // 特になし
        })
    });
    
    /** ダウンロードボタン(zip)の押下時イベント. */
    $('#download-zip').click(function() {
        //通常のアクションをキャンセル
        event.preventDefault();
        // フォームの値を取得(CSRF対策)
        var formData = $('#download-form').serializeArray();
        // ajaxでファイルダウンロード
        $.ajax({
            type: 'post',
            url: '/user/list/download/zip',
            data: formData,
            xhrFields:{
            responseType: 'blob'
            },
        })
        // ajax成功時の処理
        .done(function( data, status, jqXHR ) {
        // ファイル名
            var fileName = 'sample.zip';
        // Blob作成
            const blob = new Blob([data], {type: data.type});
        // ファイル保存関数の呼び出し
            fileSave(data, fileName);
        })
        // ajax失敗時の処理
        .fail(function( jqXHR, status, errorThrown ) {
            alert('ファイルダウンロード失敗');
        })
        // 成功しても失敗しても実行する処理
        .always(function( data, status, errorThrown ) {
            // 特になし
        })
    });
});

/** ファイル保存用関数. */
function fileSave(blob, fileName) {

    // IEかどうかで処理を分ける
    if (window.navigator.msSaveBlob) {

        // IEの場合
        window.navigator.msSaveBlob(blob, fileName);

    } else {
       // IE以外の場合
        // aタグの生成
        var a = document.createElement('a');
    
        // レスポンスからBlobオブジェクト＆URLの生成
        var blobUrl = window.URL.createObjectURL(blob);
    
        // 上で生成したaタグをHTMLに追加
        document.body.appendChild(a);
        a.style = 'display: none';
        
        // BlobオブジェクトURLをセット
        a.href = blobUrl;
        // ダウンロードさせるファイル名の生成
        a.download = fileName;
        // クリックイベント発火
        a.click();
    }
}


/**検索処理.*/
function search(){
    //formの値を取得
    var formData=$('#user-search-form').serialize();
    
    //ajax通信
    $.ajax({
        type:"GET",
        url:'/user/get/list',
        data:formData,
        dataType:'json',
        contentType:'application/json;charset=UTF-8',
        cache:false,
        timeout:5000,
    }).done(function(data){
        //ajax成功時の処理
        console.log(data);
        //JSONを変数に入れる
        userData=data;
        //DataTables作成
        createDataTables();
    }).fail(function(jqXHR,textStatus,errorThrown){
        //ajax失敗時の処理
        alert('検索処理に失敗しました');
    }).always(function(){
        //常に実行する処理(特になし)
    });
}

/**DataTables作成.*/
function createDataTables(){
    //既にDataTablesが作成されている場合
    if(table!==null){
        //DataTables破棄
        table.destroy();
    }
    
    //DataTables作成
    table=$('#user-list-table').DataTable({
        //日本語化
        language:{
            url:'/webjars/datatables-plugins/i18n/Japanese.json'
        },
        //表示データ
        data:userData,
        //データと列のマッピング
        columns:[
            {data:'userId'},//ユーザーID
            {data:'userName'},//ユーザー名
            {
                data:'birthday',//誕生日
                render:function(data,type,row){
                    var date=new Date(data);
                    var year=date.getFullYear();
                    var month=date.getMonth()+1;
                    var date=date.getDate();
                    return year + '/' + month + '/' + date;
                }
            },
            {data:'age'},//年齢
            {
                data:'gender',//性別
                render:function(data,type,row){
                    var gender='';
                    if(data===1){
                        gender='男性';
                    } else {
                        gender='女性';
                    }
                    return gender;
                }
            },
            {
                data:'userId',//詳細画面のURL
                render:function(data,type,row){
                    var url='<a href="/user/detail/' + data + '">詳細</a>';
                    return url;
                }
            },
        ]
    });
}
