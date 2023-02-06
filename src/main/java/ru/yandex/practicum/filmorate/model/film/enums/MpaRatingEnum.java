package ru.yandex.practicum.filmorate.model.film.enums;

public enum MpaRatingEnum {
	G("G", "у фильма нет возрастных ограничений"),
	//— у фильма нет возрастных ограничений,
	PG("PG", "детям рекомендуется смотреть фильм с родителями"),
	//— детям рекомендуется смотреть фильм с родителями,
	PG_13("PG-13", "детям до 13 лет просмотр нежелателен"),
	// — детям до 13 лет просмотр нежелателен,
	R("R", "лицам до 17 лет просматривать фильм можно только в присутствии взрослого"),
	//— лицам до 17 лет просматривать фильм можно только в присутствии взрослого,
	NC_17("NC-17", "лицам до 18 лет просмотр запрещён"),
	; //— лицам до 18 лет просмотр запрещён

	private final String name;
	private final String description;

	MpaRatingEnum(String name, String description) {
		this.name = name;
		this.description = description;
	}

	public static MpaRatingEnum fromValue(String v) {
		for (MpaRatingEnum c : MpaRatingEnum.values()) {
			if (c.name.equals(v)) {
				return c;
			}
		}
		return null;
	}

	public boolean equals(String name) {
		return this.toString().equals(name);
	}

	public String getDescription() {
		return description;
	}
}
