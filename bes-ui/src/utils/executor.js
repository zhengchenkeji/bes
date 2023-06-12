import H265webjsModule from '../../public/h265webjs/index'

export const createPlayerServer = (videoUrl, config) => {
  return H265webjsModule.createPlayer(videoUrl, config);
}
/**
 *
 * @param {ReturnType<typeof H265webjsModule.createPlayer>} playerInstance
 */
export const destoryPlayerServer = (playerInstance) => {
  // release playerInstance & forece Gc
  // 释放播放器
  playerInstance.release()
  playerInstance = null
}

