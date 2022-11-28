package com.pvcombank.sdk.ekyc.model

enum class CMND : TypeID {
	OLD_FRONT {
		override fun getData(): String = "vn.cmnd_old.front"
	},
	OLD_BACK {
		override fun getData(): String = "vn.cmnd_old.back"
	},
	NEW_FRONT {
		override fun getData(): String = "vn.cmnd_new.front"
	},
	NEW_BACK {
		override fun getData(): String = "vn.cmnd_new.back"
	};
}

enum class CCCD : TypeID {
	OLD_FRONT {
		override fun getData(): String = "vn.cccd.front"
	},
	OLD_BACK {
		override fun getData(): String = "vn.cccd.back"
	},
	NEW_FRONT {
		override fun getData(): String = "vn.cccd_new.front"
	},
	NEW_BACK {
		override fun getData(): String = "vn.cccd_new.back"
	};
}

enum class PASSPORT : TypeID {
	FRONT {
		override fun getData(): String {
			return "vn.passport.front"
		}
	};
}

interface TypeID {
	fun getData(): String
}