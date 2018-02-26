# elog

A simple program to generate a Debian-style changelog for Gentoo packages from
 the Gentoo GitHub repository

## Usage

```
$ lein run [pkg-specs]
```

In case the above command has empty output, you were rate-limited by the GitHub
 API servers.  In this case provide your GitHub username/password to get the
 benefits of higher rate-limits:
```
$ env ELOG_USERNAME=... ELOG_PASSWORD=... lein run [pkg-specs]
```

## License

Copyright © 2017-2018 Dennis Schridde

This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

You should have received a copy of the GNU General Public License
 along with this program.  If not, see <https://www.gnu.org/licenses/>.
