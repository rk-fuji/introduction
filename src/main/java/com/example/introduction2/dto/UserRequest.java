package com.example.introduction2.dto;

import java.io.Serializable;

import org.springframework.web.multipart.MultipartFile;

import com.example.introduction2.validator.MediaTypeImage;
import com.example.introduction2.validator.UploadFileMaxSize;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRequest implements Serializable {

	@NotEmpty(message = "名前 は必須です")
	@Size(max = 20, message = "名前 は 20文字 以内で入力してください")
	private String name;

	@NotNull(message = "年齢 は必須です")
	@PositiveOrZero(message = "年齢 は 0 以上の整数で入力してください")
	private Integer age;

	private String gender;
	private String prefecture;

	@NotEmpty(message = "住所 は必須です")
	@Size(max = 50, message = "住所 は 50文字 以内で入力してください")
	private String address;

	private String[] hobby;

	@NotEmpty(message = "自己紹介文 は必須です")
	private String introduction;

	@MediaTypeImage
	@UploadFileMaxSize
	private MultipartFile picture;

	// 保存ファイル名
	private String pictureFileName;

	/**
	 * コンストラクタ
	 */
	public UserRequest() {
		this.name = "富士太郎";
		this.age = 20;
		this.gender = "1";
		this.prefecture = "13";
		this.address = "港区六本木７－２１－７";
		this.introduction = "私の名前は、富士太郎です。\n現在、HTMLを勉強中です。\n宜しくお願い致します。";
	}
}
