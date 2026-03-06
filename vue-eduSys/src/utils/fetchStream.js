// 流式接口通用响应方法
// import Cookies from 'js-cookie'

// const token = Cookies.get('token')
const apiBaseUrl = process.env.VUE_APP_BASE_URL

export const fetchStreamResponse = async (apiName, streamRequest, onDataReceived, onComplete, onError) => {
  try {
    const response = await fetch(`${apiBaseUrl}${apiName}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        // 'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify(streamRequest)
    })

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`)
    }

    if (!response.body) {
      throw new Error('ReadableStream not supported in this browser.')
    }

    const reader = response.body.getReader()
    const decoder = new TextDecoder()
    
    let ifContinue = true
    while (ifContinue) {
      const { done, value } = await reader.read()
      if (done) {
        onComplete()    //接收完毕后调用的函数
        ifContinue=false
        break
      }

      const chunk = decoder.decode(value, { stream: true })
      onDataReceived(chunk);        //直接返回原文本块
      
    }
  } catch (error) {
    onError(error)  
  }
}