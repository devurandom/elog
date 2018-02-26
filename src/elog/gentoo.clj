; SPDX-License-Identifier: GPL-3.0-or-later
; Copyright (C) 2017-2018  Dennis Schridde

(ns elog.gentoo)

(def organisation "gentoo")
(def repository "gentoo")

; https://projects.gentoo.org/pms/6/pms.html#x1-280003.2
(def pkg-version-stregex
  "(?<base>[0-9]+(\\.[0-9]+)*)(?<subversion>[a-z])?(_(?<qualifier>alpha|beta|pre|rc|p)(?<qualifierversion>[0-9]*))?(-r(?<revision>[0-9]+))?")
(def pkg-version-regex
  (re-pattern pkg-version-stregex))

(def ebuild-filename-version-stregex
  (str pkg-version-stregex "\\.ebuild"))
(def ebuild-filename-version-regex
  (re-pattern ebuild-filename-version-stregex))
