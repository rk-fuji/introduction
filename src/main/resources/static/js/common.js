// 削除フォーム実行(自己紹介)
function deleteIntroduction(e) {
  const result = window.confirm("削除してよろしいですか？");
  if (result) {
    let form = document.getElementById("delete-form");
    form.action = "/users/" + e.dataset["id"];
    form.submit();
  }
}

// 削除フォーム実行(経歴)
function deleteCareer(e) {
  const userId = e.dataset["userid"];
  const jobCareerId = e.dataset["jobcareerid"];
  const careerId = e.dataset["careerid"];
  const url = `/users/${userId}/job_career/${jobCareerId}/careers/${careerId}`;
  const result = window.confirm("削除してよろしいですか？");
  if (result) {
    let form = document.getElementById("career-delete-form");
    form.action = url;
    form.submit();
  }
}

// 登録モーダル起動
function openCareerCreateModal(e) {
  const userId = e.dataset["userid"];
  const jobCareerId = e.dataset["jobcareerid"];
  const url = `/users/${userId}/job_career/${jobCareerId}/careers`;
  $("#create-form").attr("action", url);
}

// 編集モーダル起動
function openCareerEditModal(e) {
  const userId = e.dataset["userid"];
  const jobCareerId = e.dataset["jobcareerid"];
  const careerId = e.dataset["careerid"];
  const url = `/users/${userId}/job_career/${jobCareerId}/careers/${careerId}`;
  $.ajax({
    url: url,
    type: "get",
  })
    .done((response) => {
      console.log("--------------");
      console.log(response);
      console.log("--------------");

      // モーダルへデータをセット
      // 日付は YYYY-MM-DD の -DD を除去してセット
      let fromDate = response["fromDate"]
        ? response["fromDate"].replace("-01", "")
        : "";
      let toDate = response["toDate"]
        ? response["toDate"].replace("-01", "")
        : "";
      $("#edit-fromDate").val(fromDate);

      console.log($("#edit-fromDate").val());

      $("#edit-toDate").val(toDate);
      $("#edit-businessOutline").val(response["businessOutline"]);
      $("#edit-businessOutlineDescription").val(
        response["businessOutlineDescription"]
      );
      $("#edit-role").val(response["role"]);
      $("#edit-roleDescription").val(response["roleDescription"]);
      $("#edit-useLanguage").val(response["useLanguage"]);
      $("#edit-useDatabase").val(response["useDatabase"]);
      $("#edit-useServer").val(response["useServer"]);
      $("#edit-other").val(response["other"]);
      $("#edit-responsibleProcess").val(response["responsibleProcess"]);

      $("#edit-form").attr("action", url);

      // モーダル起動
      let myModal = new bootstrap.Modal(
        document.getElementById("careerEditModal")
      );
      var modalToggle = document.getElementById("careerEditModal");
      myModal.show(modalToggle);
    })
    .fail((e) => {
      console.error(e);
    });
}

// 検索種別変更
function changeSearchType(event) {
  const selectedIndex = event.selectedIndex;
  const selectedItem = event.options[selectedIndex].value;
  switch (selectedItem) {
    case "gender":
      // 初期化
      $("#search-select").empty();

      // 設定(選択項目)
      $("#search-select").append(
        $("<option>").val("1").text("男性"),
        $("<option>").val("2").text("女性")
      );
      break;
    case "prefecture":
      $.ajax({
        url: "/prefectures",
        type: "get",
      })
        .done((response) => {
          console.log("--------------");
          console.log(response);
          console.log("--------------");

          let select = $("#search-select");
          select.empty();
          response.forEach((prefecture, index) => {
            let option = $("<option>").val(prefecture.id).text(prefecture.name);
            if (index === 0) {
              option.prop("selected", true);
            }
            select.append(option);
          });
        })
        .fail((e) => {
          console.error(e);
        });

      break;
  }
}
