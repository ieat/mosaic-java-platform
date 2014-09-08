#!/dev/null

if ! test "${#}" -eq 0 ; then
	echo "[ee] invalid arguments; aborting!" >&2
	exit 1
fi

cat <<EOS

${_package_name}@requisites : \
		pallur-packages@java \
		pallur-packages@maven \
		pallur-packages@jzmq \
		pallur-environment

${_package_name}@prepare : ${_package_name}@requisites
	!exec ${_scripts}/prepare

${_package_name}@package : ${_package_name}@compile
	!exec ${_scripts}/package

${_package_name}@compile : ${_package_name}@prepare
	!exec ${_scripts}/compile

${_package_name}@deploy : ${_package_name}@package
	!exec ${_scripts}/deploy

EOS

exit 0
